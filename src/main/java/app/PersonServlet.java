package app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class PersonServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private Phonebook phonebook;

	@Override
	public void init() throws ServletException {
		super.init();

		try {
			this.phonebook = new Phonebook();
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

	// Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
	private String validatePersonFMLName(Person person)
	{
		String error_message = "";

		if (!person.validateFMLNamePart(person.getName(), false)) {
			error_message += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getSurname(), false)) {
			error_message += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
			error_message += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}
		return error_message;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try {
			this.phonebook = new Phonebook();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		request.setCharacterEncoding("UTF-8");
		request.setAttribute("phonebook", this.phonebook);

		HashMap<String,String> jsp_parameters = new HashMap<String,String>();

		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePersonAdd.jsp");
		RequestDispatcher dispatcher_for_manager_edit = request.getRequestDispatcher("/ManagePersonEdit.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");

		String action = request.getParameter("action");
		String id = request.getParameter("id");

		if ((action == null)&&(id == null)) {
			request.setAttribute("jsp_parameters", jsp_parameters);
			dispatcher_for_list.forward(request, response);
		} else {
			switch (action)
			{ case "add":
					Person empty_person = new Person();

					jsp_parameters.put("current_action", "add");
					jsp_parameters.put("next_action", "add_go");
					jsp_parameters.put("next_action_label", "Добавить");

					request.setAttribute("person", empty_person);
					request.setAttribute("jsp_parameters", jsp_parameters);

					dispatcher_for_manager.forward(request, response);
					break;

				case "edit":
					Person editable_person = this.phonebook.getPerson(id);

					jsp_parameters.put("current_action", "edit");
					jsp_parameters.put("next_action", "edit_go");
					jsp_parameters.put("next_action_label", "Сохранить");

					request.setAttribute("person", editable_person);
					request.setAttribute("jsp_parameters", jsp_parameters);

					dispatcher_for_manager_edit.forward(request, response);
					break;

				case "delete":

					if (phonebook.deletePerson(id)) {
						jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
						jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
					} else {
						jsp_parameters.put("current_action_result", "DELETION_FAILURE");
						jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
					}
					request.setAttribute("jsp_parameters", jsp_parameters);

					dispatcher_for_list.forward(request, response);
					break;

				case "edit_number":
					Person edit_person_number = this.phonebook.getPerson(id);

					jsp_parameters.put("current_action", "edit");
					jsp_parameters.put("next_action", "edit_go");
					jsp_parameters.put("next_action_label", "Сохранить");

					request.setAttribute("person", edit_person_number.getId());
					request.setAttribute("jsp_parameters", jsp_parameters);
					request.getRequestDispatcher("/ManageNumber.jsp").forward(request, response);
					break;
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			this.phonebook = new Phonebook();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		request.setCharacterEncoding("UTF-8");
		request.setAttribute("phonebook", this.phonebook);

		HashMap<String,String> jsp_parameters = new HashMap<String,String>();

		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePersonAdd.jsp");
		RequestDispatcher dispatcher_for_manager_edit = request.getRequestDispatcher("/ManagePersonEdit.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");


		String add_go = request.getParameter("add_go");
		String edit_go = request.getParameter("edit_go");
		String id = request.getParameter("id");

		if (add_go != null) {
			Person new_person = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));
			String error_message = this.validatePersonFMLName(new_person);

			if (error_message.equals("")) {
				if (this.phonebook.addPerson(new_person)) {
					jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка добавления");
				}
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			} else {
				jsp_parameters.put("current_action", "add");
				jsp_parameters.put("next_action", "add_go");
				jsp_parameters.put("next_action_label", "Добавить");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", new_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			}
		}

		if (edit_go != null) {
			Person updatable_person = this.phonebook.getPerson(request.getParameter("id"));
			updatable_person.setName(request.getParameter("name"));
			updatable_person.setSurname(request.getParameter("surname"));
			updatable_person.setMiddlename(request.getParameter("middlename"));

			// Валидация ФИО.
			String error_message = this.validatePersonFMLName(updatable_person);

			if (error_message.equals("")) {
				if (this.phonebook.updatePerson(id, updatable_person)) {
					jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
				} else {
					jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка обновления");
				}

				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			} else {
				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");
				jsp_parameters.put("error_message", error_message);

				request.setAttribute("person", updatable_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				dispatcher_for_list.forward(request, response);
			}
		}
	}
}