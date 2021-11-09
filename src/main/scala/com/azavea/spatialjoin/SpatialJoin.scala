package com.azavea.spatialjoin

import geotrellis.vector._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.jts.JTSTypes.GeometryTypeInstance

object SpatialJoin {

  def apply(left: DataFrame, right: DataFrame): DataFrame = {
    val leftGeomOpt = identifyGeomColumn(left)
    val righttGeomOpt = identifyGeomColumn(right)

    assert(leftGeomOpt isDefined, "Left dataframe must have unique geometry column")
    assert(rightGeomOpt isDefined, "Right dataframe must have unique geometry column")

    ???
  }

  def identifyGeomColumn(df: DataFrame): Option[String] =
    df.schema.filter{ f => GeometryTypeInstance.equals(f.dataType) } match {
      case List() => None
      case List(field) => field
      case _ => None
    }.map(_.name)

}
