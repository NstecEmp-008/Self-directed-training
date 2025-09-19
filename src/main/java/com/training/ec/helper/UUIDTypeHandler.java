package com.training.ec.helper; // プロジェクト内の独自パッケージに配置

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * PostgreSQL の UUID 型と Java の java.util.UUID 型を変換する TypeHandler
 * MyBatis に「どうやってこの型をDBとマッピングするか」を教えるクラス
 */
public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    /**
     * PreparedStatement（SQLを実行する前の準備）に値をセットする処理
     * Java の UUID を PostgreSQL の UUID 型（OTHER型として扱う）に変換する
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        // ps.setObject(index, 値, SQL型)
        // PostgreSQL の UUID 型は JDBC では OTHER 扱いになるので Types.OTHER を指定する
        ps.setObject(i, parameter, Types.OTHER);
    }

    /**
     * SELECT 結果（ResultSet）からカラム名で値を取得する処理
     * PostgreSQL の UUID → Java の UUID に変換
     */
    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName); // DB から値を取り出す
        return obj != null ? (UUID) obj : null; // nullチェックして UUID にキャスト
    }

    /**
     * SELECT 結果（ResultSet）からカラム番号で値を取得する処理
     * 使う場面は少ないが、内部的に必要なので実装する
     */
    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object obj = rs.getObject(columnIndex);
        return obj != null ? (UUID) obj : null;
    }

    /**
     * ストアドプロシージャの戻り値から値を取得する処理
     * CallableStatement を使う場合に必要（今回はほぼ使わない）
     */
    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object obj = cs.getObject(columnIndex);
        return obj != null ? (UUID) obj : null;
    }
}
