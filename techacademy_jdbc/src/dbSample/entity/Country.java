package dbSample.entity;

public class Country {

}
//フィールド
private String name;
private int population;

// 引数なしコンストラクタ
public Country() {

}

// 引数ありコンストラクタ
public Country(String name, int population) {
    this.name = name;
    this.population = population;
}

// getter/setter
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public int getPopulation() {
    return population;
}

public void setPopulation(int population) {
    this.population = population;
}
}
package dbSample.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // データベース接続と結果取得のための変数
    private static Connection con;

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // 1. ドライバのクラスをJava上で読み込む
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2. DBと接続する
        con = DriverManager.getConnection(
            "jdbc:mysql://localhost/world?useSSL=false&allowPublicKeyRetrieval=true",
            "root",
            "password"
        );
        // "password"の部分は，ご自身でrootユーザーに設定したものを記載してください。

        return con;
    }

    public static void close() {
        // 7. 接続を閉じる
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
package dbSample.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dbSample.entity.Country;
import dbSample.util.DatabaseManager;

public class CountryDAO {
    // データベース接続と結果取得のための変数
    private PreparedStatement pstmt;
    private ResultSet rs;

    public List<Country> getCountryFromName(String name) {
        // メソッドの結果として返すリスト
        List<Country> results = new ArrayList<Country>();

        try {
            // 1,2. ドライバを読み込み、DBに接続
            Connection con = DatabaseManager.getConnection();

            // 3. DBとやりとりする窓口（Statementオブジェクト）の作成
            String sql = "select * from country where Name = ?";
            pstmt = con.prepareStatement(sql);

            // 4, 5. Select文の実行と結果を格納／代入
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();

            // 6. 結果を表示する
            while (rs.next()) {
                // 1件ずつCountryオブジェクトを生成して結果を詰める
                Country country = new Country();
                country.setName(rs.getString("Name"));
                country.setPopulation(rs.getInt("Population"));

                // リストに追加
                results.add(country);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            if( rs != null ){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if( pstmt != null ){
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DatabaseManager.close();
        }
        return results;
    }
}
package dbSample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import dbSample.dao.CountryDAO;
import dbSample.entity.Country;

public class DbConnectSample06 {

    public static void main(String[] args) {
        // Countryクラスにアクセスするため、CountryDAOをインスタンス化
        CountryDAO dao = new CountryDAO();

        // 検索用キーワードを入力
        System.out.print("検索キーワードを入力してください > ");
        String name = keyIn();

        // 入力された値を引数に指定し、検索処理を実行し、Listオブジェクトを取得
        List<Country> list = dao.getCountryFromName(name);

        // 取得したListオブジェクトを順番に取り出し、出力
        for(Country item : list){
            System.out.println(item.getName());
            System.out.println(item.getPopulation());
        }
    }

    /*
    * キーボードから入力された値をStringで返す 引数：なし 戻り値：入力された文字列
    */
    private static String keyIn() {
        String line = null;
        try {
            BufferedReader key = new BufferedReader(new InputStreamReader(System.in));
            line = key.readLine();
        } catch (IOException e) {

        }
        return line;
    }
}
