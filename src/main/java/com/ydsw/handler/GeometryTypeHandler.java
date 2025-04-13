package com.ydsw.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;
import java.sql.*;

public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final WKTReader wktReader = new WKTReader(geometryFactory);
    private final WKTWriter wktWriter = new WKTWriter();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Geometry geometry, JdbcType jdbcType)
            throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("geometry");
        pgObject.setValue(convertToEWKT(geometry));
        ps.setObject(i, pgObject);
    }

    // 将 Geometry 转换为 EWKT 格式（包含 SRID）
    private String convertToEWKT(Geometry geometry) {
        int srid = geometry.getSRID();
        String wkt = wktWriter.write(geometry);
        return "SRID=" + srid + ";" + wkt;
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return parseEWKT(rs.getString(columnName));
    }

    @Override
    public Geometry getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return parseEWKT(rs.getString(columnIndex));
    }

    @Override
    public Geometry getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return parseEWKT(cs.getString(columnIndex));
    }

    // 从 EWKT 解析为 Geometry
    private Geometry parseEWKT(String ewkt) {
        if (ewkt == null) return null;
        try {
            // 提取 SRID 和 WKT
            String[] parts = ewkt.split(";", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid EWKT format: " + ewkt);
            }
            String sridPart = parts[0];
            String wkt = parts[1];
            int srid = Integer.parseInt(sridPart.replace("SRID=", ""));

            // 解析几何对象并设置 SRID
            Geometry geometry = wktReader.read(wkt);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException | NumberFormatException e) {
            throw new RuntimeException("Failed to parse EWKT: " + ewkt, e);
        }
    }
}