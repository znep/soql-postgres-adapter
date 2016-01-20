package com.socrata.pg.soql

import SqlizerTest._

// scalastyle:off null
class SqlizerLocationTest extends SqlizerTest {

  test("location latitude") {
    val soql = "select location_latitude(location)"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be ("SELECT (ST_Y(location_geom)::numeric) FROM t1")
    setParams.length should be (0)
  }

  test("location longitude") {
    val soql = "select location_longitude(location)"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be ("SELECT (ST_X(location_geom)::numeric) FROM t1")
    setParams.length should be (0)
  }

  test("location human_address") {
    val soql = "select location_human_address(location)"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be ("SELECT (location_address) FROM t1")
    setParams.length should be (0)
  }

  test("location point") {
    val soql = "select location::point"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be ("SELECT ST_AsBinary((location_geom)) FROM t1")
    setParams.length should be (0)
  }

  test("location within_circle") {
    val soql = "select case_number where within_circle(location, 1.0, 2.0, 30)"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be (
      """SELECT case_number FROM t1
        | WHERE ((ST_within((location_geom), ST_Buffer(ST_MakePoint(?, ?)::geography, ?)::geometry)))"""
        .stripMargin.replaceAll("\n", ""))
    val params = setParams.map { (setParam) => setParam(None, 0).get }
    params should be (Seq(2.0, 1.0, 30d).map(BigDecimal.valueOf(_)))
  }

  test("location within_box") {
    val soql = "select case_number where within_box(location, 1.0, 2.0, 3.0, 4.0)"
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be (
      """SELECT case_number FROM t1
        | WHERE ((ST_MakeEnvelope(?, ?, ?, ?, 4326) ~ (location_geom)))"""
        .stripMargin.replaceAll("\n", ""))
    val params = setParams.map { (setParam) => setParam(None, 0).get }
    params should be (Seq(2.0, 3.0, 4.0, 1.0).map(BigDecimal.valueOf(_)))
  }

  test("subcolumn subscript converted") {
    val soql = """SELECT location.longitude as longitude WHERE location.latitude = 1.1 order by longitude"""
    val ParametricSql(Seq(sql), setParams) = sqlize(soql, CaseSensitive)
    sql should be ("SELECT (ST_X(location_geom)::numeric) FROM t1 WHERE ((ST_Y(location_geom)::numeric) = ?) ORDER BY (ST_X(location_geom)::numeric) nulls last")
    val params = setParams.map { (setParam) => setParam(None, 0).get }
    params should be (Seq(BigDecimal.valueOf(1.1)))
  }
}