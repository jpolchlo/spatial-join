package com.azavea.spatialjoin

import geotrellis.vector._
import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec

class SpatialJoinSpec extends TestEnvironment
                         with Matchers
                         with AnyFunSpec {

  describe("Spatial Join") {
    it("should identify geometry columns") {
      case class WithPoint(other: Int, geom: Point)
      case class WithMultiPoly(geom: MultiPolygon, x: Double, s: String)

      val df1 = spark.createDataset[WithPoint](List()).toDF
      val df2 = spark.createDataset[WithMultiPoly](List()).toDF

      SpatialJoin.identifyGeomColumn(df1) should be (Some("geom"))
      SpatialJoin.identifyGeomColumn(df2) should be (Some("geom"))
    }

    it("should fail for multiple geometry columns") {
      case class TooManyGeoms(pt: Point, poly: Polygon)

      val df1 = spark.createDataset[TooManyGeoms](List()).toDF

      SpatialJoin.identifyGeomColumn(df1) should be (None)
    }

    it("should fail for multiple geometry columns") {
      case class NoGeoms(i: Int, d: Double)

      val df1 = spark.createDataset[NoGeoms](List()).toDF
      SpatialJoin.identifyGeomColumn(df1) should be (None)
    }
  }
}
