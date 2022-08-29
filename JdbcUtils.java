import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TenantJdbcServiceImpl implements TenantJdbcService {

    /**
     * JDBC 연결후 SQL 실행
     * @param newSqlFile newSqlFile 변경 한 SQL 파일
     */
    public void jdbcConnection(String newSqlFile) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            List<String> sqlList = sqlFile(newSqlFile);

            String dbName = sqlList.get(0).trim();
            System.out.println("DB NAME IS "+dbName);
            String url = "jdbc:mariadb://localhost:3306/"
                    + dbName + "?autoReconnect=true&serverTimezone=Asia/Seoul"
                    + "&connectTimeout=10000&socketTimeout=60000";

            connection = DriverManager.getConnection(url, "root", "1234");

            statement = connection.createStatement();

            for (int i = 1; i < sqlList.size(); i++) {
                resultSet = statement.executeQuery(sqlList.get(i));
            }
        } catch (SQLException e) {
            System.out.println("에러 " + e);
        }
        finally {
            // Connection, Statement, ResultSet 객체를 닫는 코드.
            // isClosed(), close()는 SQLException 발생 가능 - try-catch로 예외처리.
            try {
                if (connection != null && !connection.isClosed()) {
                    // Connection이 존재하고, 닫혀있지 않다면
                    connection.close();
                }
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
                if (resultSet != null && !resultSet.isClosed()) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SQL 파일 읽어 List 로 출력
     * @param path SQL 파일 경로
     * @return SQL 파일 내용 저장 List
     */
    public List sqlFile(String path) {

        Path filepath = Paths.get(path);
        List<String> sqlList = new ArrayList<String>();
        Charset cs = StandardCharsets.UTF_8;
        List<String> list = new ArrayList<String>();

        try {
            list = Files.readAllLines(filepath,cs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String temp = "";
        for (String line : list) {
            if(!line.matches("^--.*")) {
                temp += line.replaceAll("\n"," ").replaceAll(";","")
                        .replaceAll("USE","")
                        .replaceAll("--.*","");
                if(line.indexOf(";") > 0) {
                    sqlList.add(temp);
                    temp="";
                }
            }
        }

        return sqlList;
    }
}
