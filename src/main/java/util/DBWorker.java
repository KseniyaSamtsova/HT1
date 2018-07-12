package util;

import java.sql.*;

public class DBWorker {
	private Statement statement;
	private Connection connect;
	// Количество рядов таблицы, затронутых последним запросом.
	private Integer affected_rows = 0;

	// Значение автоинкрементируемого первичного ключа, полученное после
	// добавления новой записи.
	private Integer last_insert_id = 0;

	// Указатель на экземпляр класса.
	private static DBWorker instance = null;


	public static DBWorker getInstance()
	{
		if (instance == null)
		{
			instance = new DBWorker();
		}

		return instance;
	}

	// "Заглушка", чтобы экземпляр класса нельзя было получить напрямую.
	private DBWorker()
	{
	 // Просто "заглушка".
	}

	// Выполнение запросов на выборку данных.
	public ResultSet getDBData(String query)
	{
		try
		{
		    Class.forName("com.mysql.cj.jdbc.Driver");
			tryGetStatement();
			ResultSet resultSet = this.statement.executeQuery(query);
			return resultSet;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("null on getDBData()!");
		return null;
	}

	// Выполнение запросов на модификацию данных.
	public Integer changeDBData(String query)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			tryGetStatement();
			this.affected_rows = this.statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

			// Получаем last_insert_id() для операции вставки.
            ResultSet rs = this.statement.getGeneratedKeys();
            if (rs.next()){
            	this.last_insert_id = rs.getInt(1);
            }

			return this.affected_rows;
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}

		System.out.println("null on changeDBData()!");
		return null;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++
	// Геттеры и сеттеры.
	public Integer getAffectedRowsCount()
	{
		return this.affected_rows;
	}

	public Integer getLastInsertId()
	{
		return this.last_insert_id;
	}
	// Геттеры и сеттеры.
	// -------------------------------------------------

	private void tryGetStatement() {
		try {
			connect = DriverManager.getConnection("jdbc:mysql://localhost/phonebook?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "admin");

			statement = connect.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}

