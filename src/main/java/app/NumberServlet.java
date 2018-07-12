package app;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;


public class NumberServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PhonebookNumber phonebookNumber;
    private Phonebook phonebook;


    public NumberServlet()
    {
        super();

        try
        {
            this.phonebookNumber = PhonebookNumber.getInstance();
            this.phonebook= new Phonebook();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private String validatePhoneNumber(Number number )
    {
        String error_message = "";

        if (!number.validatePhoneNumber(number.getPhoneNumber(),false))
        {
            error_message += "Номер телефона должн быть строкой от 2 до 50 символов из цифр, знаков +  -  #.<br />";
        }
        if (!number.validatePhoneNumber(number.getPhoneNumber(),true))
        {
            error_message += "Номер телефона должн быть строкой от 0 до 50 символов из цифр, знаков +  -  #.<br />";
        }

        return error_message;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

        request.setCharacterEncoding("UTF-8");
        HashMap<String,String> jsp_parameters = new HashMap<String,String>();
        HttpSession session=request.getSession();

        RequestDispatcher dispatcher_for_manager_edit_number = request.getRequestDispatcher("/ManageNumberEdit.jsp");
        RequestDispatcher dispatcher_for_manager_number = request.getRequestDispatcher("/ManageNumber.jsp");
        RequestDispatcher dispatcher_for_manager_number_add = request.getRequestDispatcher("/ManageNumberAdd.jsp");
       // RequestDispatcher dispatcher_for_manager_forward = request.getRequestDispatcher("/ManagePersonForward.jsp");

        String action = request.getParameter("action");
        String id = request.getParameter("id");
        String ownerId=request.getParameter("owner");
        String fullName=this.phonebook.getPerson(ownerId).getSurname()+" "+this.phonebook.getPerson(ownerId).getName()
                +" "+this.phonebook.getPerson(ownerId).getMiddlename();

        if ((action == null)&&(id == null))
        {
            request.setAttribute("jsp_parameters",jsp_parameters);
            request.setAttribute("owner",ownerId);

            dispatcher_for_manager_number.forward(request, response);
        }

        else
        {
            switch (action) {
                case "add":
                    Number empty_number = new Number();
                    empty_number.setOwner(request.getParameter("owner"));

                    jsp_parameters.put("current_action", "add");
                    jsp_parameters.put("next_action", "add_go");
                    jsp_parameters.put("next_action_label", "Сохранить");

                    request.setAttribute("number", empty_number);
                    request.setAttribute("jsp_parameters", jsp_parameters);
                    request.setAttribute("fullName", fullName);

                    dispatcher_for_manager_number_add.forward(request, response);
                    break;

                case "edit":
                    Number editable_number = this.phonebookNumber.getPhoneNumber(id);

                    jsp_parameters.put("current_action", "edit");
                    jsp_parameters.put("next_action", "edit_go");
                    jsp_parameters.put("next_action_label", "Сохранить номер");

                    request.setAttribute("number", editable_number);
                    request.setAttribute("owner",ownerId);
                    request.setAttribute("jsp_parameters", jsp_parameters);
                    request.setAttribute("fullName", fullName);

                    dispatcher_for_manager_edit_number.forward(request, response);
                    break;

                case "delete":

                    if (phonebookNumber.deletePhoneNumber(id)) {
                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }

                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
                    }

                    Person editable_person = this.phonebook.getPerson(ownerId);
                    session.setAttribute("jsp_parameters", jsp_parameters);
                    request.setAttribute("person",editable_person);
                    request.setAttribute("fullName", fullName);


                    response.sendRedirect("/?action=edit&id="+ownerId);
                    break;
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("phonebook", this.phonebook);
        HttpSession session=request.getSession();

        HashMap<String,String> jsp_parameters = new HashMap<String,String>();

        String add_go = request.getParameter("add_go");
        String edit_go = request.getParameter("edit_go");
        String id = request.getParameter("id");
        Person editable_person = this.phonebook.getPerson(request.getParameter("owner"));

        if (add_go != null) {
            Number new_number =new Number(request.getParameter("owner"),request.getParameter("number"));

            String error_message = this.validatePhoneNumber(new_number);

            if (error_message.equals("")) {
                if (this.phonebookNumber.addPhoneNumber(new_number)) {
                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }
            }
            else {
                jsp_parameters.put("current_action", "add");
                jsp_parameters.put("next_action", "add_go");
                jsp_parameters.put("next_action_label", "Добавить");
                jsp_parameters.put("error_message", error_message);
            }
            session.setAttribute("jsp_parameters", jsp_parameters);
            request.setAttribute("person",editable_person);

            response.sendRedirect("/?action=edit&id="+editable_person.getId());
        }

        if (edit_go != null) {
            Number updatable_number = this.phonebookNumber.getPhoneNumber(id);
            updatable_number.setPhoneNumber(request.getParameter("number"));

            String error_message = this.validatePhoneNumber(updatable_number);

            if (error_message.equals("")) {
                if (this.phonebookNumber.updatePhoneNumber(updatable_number)) {
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                } else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }
            } else {

                jsp_parameters.put("current_action", "edit");
                jsp_parameters.put("next_action", "edit_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);
            }

            session.setAttribute("jsp_parameters", jsp_parameters);
            request.setAttribute("person",editable_person);

            response.sendRedirect("/?action=edit&id="+editable_person.getId());
        }
    }
}
