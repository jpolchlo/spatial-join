package com.azavea.spatialjoin

import geotrellis.vector._
import org.apache.spark.sql.{DataFrame, Encoder, Row}
import org.apache.spark.sql.catalyst.{DeserializerBuildHelper, SerializerBuildHelper, WalkedTypePath}
import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}
import org.apache.spark.sql.catalyst.expressions.{BoundReference, Expression, GenericRowWithSchema}
import org.apache.spark.sql.catalyst.expressions.objects.ValidateExternalType
import org.apache.spark.sql.expressions.Aggregator
import org.apache.spark.sql.functions.{col, explode, udf}
import org.apache.spark.sql.jts.JTSTypes.GeometryTypeInstance
import org.apache.spark.sql.types.{ArrayType, ObjectType, StructField, StructType}
import org.locationtech.geomesa.spark.jts.{udf => _, _}
import org.locationtech.jts.geom.{Envelope}
import org.locationtech.jts.index.strtree.{AbstractNode, ItemBoundable}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object SpatialJoin {

  def apply(left: DataFrame, right: DataFrame): DataFrame = {
    import left.sparkSession.implicits._

    val leftGeomOpt = identifyGeomColumn(left)
    val rightGeomOpt = identifyGeomColumn(right)

    assert(leftGeomOpt isDefined, "Left dataframe must have unique geometry column")
    assert(rightGeomOpt isDefined, "Right dataframe must have unique geometry column")

    // 1. Determine spatial resolution
    //    a. Sample 1% of data
    //    b. Build STRtree from sample
    // 2. Key geometries
    //    a. Determine which r-tree leaves the geometry lies in
    //    b. For each intersecting leaf, build a key as the address of the tree node
    // 3.

    val leftGeomCol = col(leftGeomOpt.get)
    val rightGeomCol = col(rightGeomOpt.get)
    val toIndex = left.sample(0.01).select(leftGeomCol).as[Geometry].collect
    val rtree = convertTree(SpatialIndex.fromExtents(toIndex)(_.extent).rtree.getRoot)

    // We now have a minimal, queryable, broadcastable tree structure to use for keying

    val keyName = keyColumnName(left.schema ++ right.schema)
    val keyGeom = udf{ g: Geometry => getKeys(rtree)(g) }
    left
      .withColumn(keyName, explode(keyGeom(leftGeomCol)))
      .join(
        right.withColumn(keyName, explode(keyGeom(rightGeomCol))),
        keyName
      )
      .groupByKey(_.getAs[Int](leftGeomOpt.get))

    ???
  }

  def identifyGeomColumn(df: DataFrame): Option[String] =
    (df.schema.toList.filter{ f => GeometryTypeInstance.equals(f.dataType) } match {
      case List() => None
      case List(field) => Some(field)
      case _ => None
    }).map(_.name)

  def keyColumnName(fields: Seq[StructField]): String = {
    val inUse = fields.map(_.name).toSet
    var i = 0
    while (inUse.contains("_" * i + "key")) i += 1
    "_" * i + "key"
  }

  sealed trait RTreeNode {
    val ex: Extent
    val hash: Int
    def query(g: Geometry): Seq[RTreeNode]
  }
  case class InnerNode(
    ex: Extent,
    hash: Int,
    children: Seq[RTreeNode]
  ) extends RTreeNode {
    def query(g: Geometry): Seq[RTreeNode] = {
      children.filter(_.ex.intersects(g)).flatMap(_.query(g))
    }
  }
  case class LeafNode(ex: Extent, hash: Int) extends RTreeNode {
    def query(g: Geometry): Seq[RTreeNode] = Seq(this)
  }

  def convertTree(node: AbstractNode): RTreeNode = node.getLevel match {
    case 0 =>
      val env = node.getBounds.asInstanceOf[Envelope]
      LeafNode(Extent(env), env.hashCode)
    case _ =>
      val env = node.getBounds.asInstanceOf[Envelope]
      val children = node.getChildBoundables.asScala.asInstanceOf[Seq[AbstractNode]]
      InnerNode(Extent(env), env.hashCode, children.map(convertTree(_)))
  }

  def getKeys(root: RTreeNode)(g: Geometry): Seq[Int] = {
    val res = root.query(g).map(_.hash)
    if (res.isEmpty)
      Seq(Int.MinValue)
    else
      res
  }

  case class Joiner(indexCol: String, geomCol: String, schema: StructType) extends Aggregator[Row, Seq[Row], Seq[Row]] {
    override def zero: Seq[Row] = Seq[Row]()

    override def reduce(incoming: Row, acc: Seq[Row]): Seq[Row] = incoming +: acc

    override def merge(a: Seq[Row], b: Seq[Row]): Seq[Row] = a ++ b

    override def bufferEncoder: Encoder[Seq[Row]] = {
      val wrappedExpression = BoundReference(0, ArrayType(schema, false), false)
      val inputObject = ValidateExternalType(wrappedExpression, schema)

      val serializer = SerializerBuildHelper.createSerializerForMapObjects(
        inputObject,
        ObjectType(classOf[Object]),
        element => {
          val value = RowEncoder(schema).objSerializer
          DeserializerBuildHelper.expressionWithNullSafety(value, false, WalkedTypePath())
        }
      )
      val deserializer: Expression = ???

      ExpressionEncoder[Seq[Row]](serializer, deserializer, ClassTag(classOf[Seq[Row]]))
    }
  }

}
