package com.ydsw.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.postgresql.util.PGobject;
import java.sql.*;

public class MultiLineStringTypeHandler extends BaseTypeHandler<MultiLineString> {

    private final WKTReader wktReader = new WKTReader();
    private final WKTWriter wktWriter = new WKTWriter();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    MultiLineString parameter, JdbcType jdbcType)
            throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("geometry");
        pgObject.setValue(convertToEWKT(parameter));
        ps.setObject(i, pgObject);
    }

    // 将 MultiLineString 转换为 EWKT 格式（包含 SRID）
    private String convertToEWKT(MultiLineString multiLineString) {
        int srid = multiLineString.getSRID();
        String wkt = wktWriter.write(multiLineString);
        return "SRID=" + srid + ";" + wkt;
    }

    @Override
    public MultiLineString getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return parseEWKT(rs.getString(columnName));
    }

    @Override
    public MultiLineString getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return parseEWKT(rs.getString(columnIndex));
    }

    @Override
    public MultiLineString getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return parseEWKT(cs.getString(columnIndex));
    }

    // 从 EWKT 解析为 MultiLineString
    private MultiLineString parseEWKT(String ewkt) {
        if (ewkt == null) return null;
        try {
            String sridPart = ewkt.substring(0, ewkt.indexOf(';'));
            int srid = Integer.parseInt(sridPart.replace("SRID=", ""));
            String wkt = ewkt.substring(ewkt.indexOf(';') + 1);

            MultiLineString multiLineString = (MultiLineString) wktReader.read(wkt);
            multiLineString.setSRID(srid);
            return multiLineString;
        } catch (ParseException | NumberFormatException e) {
            throw new RuntimeException("Failed to parse EWKT: " + ewkt, e);
        }
    }
}