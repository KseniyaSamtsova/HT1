package app;

import util.DBWorker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PhonebookNumber {

    // Хранилище записей о людях.
    private HashMap<String, Number> phoneNumbers = new HashMap<String, Number>();


    // Объект для работы с БД.
    private DBWorker db = DBWorker.getInstance();

    // Указатель на экземпляр класса.
    private static PhonebookNumber instance = null;
    // Метод для получения экземпляра класса (реализован Singleton).
    public static PhonebookNumber getInstance() throws ClassNotFoundException, SQLException
    {
        if (instance == null)
        {
            instance = new PhonebookNumber();
        }

        return instance;
    }

    // При создании экземпляра класса из БД извлекаются все записи.
    public PhonebookNumber() throws ClassNotFoundException, SQLException
    {
        ResultSet db_data = this.db.getDBData("SELECT * FROM `phone`");
        while (db_data.next()) {
            this.phoneNumbers.put(db_data.getString("id"), new Number(db_data.getString("id"),db_data.getString("owner"), db_data.getString("number")));

        }
    }

    // Добавление записи о номере телефона.
    public boolean addPhoneNumber(Number number)
    {
        ResultSet db_result;
        String query;
        int owner_id=Integer.parseInt(number.getOwner());
        query = "INSERT INTO `phone` (`owner`, `number`) VALUES ('" + owner_id+"', '" + number.getPhoneNumber() +"')";

        Integer affected_rows = this.db.changeDBData(query);

        // Если добавление прошло успешно...
        if (affected_rows > 0)
        {
            number.setId(this.db.getLastInsertId().toString());

            // Добавляем запись о номере телефона в общий список.
            this.phoneNumbers.put(number.getId(),number);

            return true;
        }
        else
        {
            return false;
        }
    }


    // Обновление записи о номере телефона.
    public boolean updatePhoneNumber(Number number)
    {
        Integer id_filtered = Integer.parseInt(number.getId());
        String query = "";
        query = "UPDATE `phone` SET `number` = '" + number.getPhoneNumber() + "' WHERE `id` = " + id_filtered;

        Integer affected_rows = this.db.changeDBData(query);

        // Если обновление прошло успешно...
        if (affected_rows > 0)
        {
            // Обновляем запись о номере телефона в общем списке.
            this.phoneNumbers.put(number.getId(), number);
            return true;
        }
        else
        {
            return false;
        }
    }


    // Удаление записи о номере телефона.
    public boolean deletePhoneNumber(String id)
    {
        if ((id != null)&&(!id.equals("null")))
        {
            int filtered_id = Integer.parseInt(id);

            Integer affected_rows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + filtered_id);

            // Если удаление прошло успешно...
            if (affected_rows > 0)
            {
                // Удаляем запись о номере телефона из общего списка.
                this.phoneNumbers.remove(id);
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры
    public HashMap<String, Number> getContents()
    {
        return phoneNumbers;
    }

    public Number getPhoneNumber(String id)
    {
        return this.phoneNumbers.get(id);
    }


    // Геттеры и сеттеры
    // -----------------------------------------

}