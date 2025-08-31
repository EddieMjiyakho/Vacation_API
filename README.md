## 🌴 Vacation Request Management API


**📖 Overview**
---

The Vacation Request Management API is a backend application built with Java and Spring Boot. It helps organizations streamline the process of handling employee vacation requests by providing endpoints for both employees and managers.

This project was developed as part of my personal portfolio to showcase my skills in:

- Java & Spring Boot development

- REST API design

- Database modeling with JPA

- Test-driven development (TDD)

- Clean architecture & maintainability




**👨‍💻 My Role & Contributions**
---

- Designed the data model (Employee, Manager, VacationRequest) and relationships.

- Implemented RESTful APIs for employees and managers.

- Built business logic for vacation day tracking, overlapping requests, and request approval workflow.

- Applied Java best practices: layered architecture, DTOs, validation.

- Wrote unit and integration tests with JUnit & Mockito.

- Documented APIs to ensure clarity and maintainability.




**🚀 Features:**
---

For Employees:

- Submit new vacation requests (up to 30 days per year).

- View requests filtered by status (approved, pending, rejected).

- Check remaining vacation days.

For Managers:

- View an overview of all requests.

- Filter requests by status (pending, approved).

- View requests by individual employee.

- Detect overlapping vacation requests.

- Approve or reject vacation requests.




**🗄️ Example Request**
---

json
{
  "id": 1,
  "author": "WORKER_ID",
  "status": "pending",
  "resolved_by": null,
  "request_created_at": "2025-01-09T12:57:13.506Z",
  "vacation_start_date": "2025-01-24T00:00:00.000Z",
  "vacation_end_date": "2025-02-04T00:00:00.000Z"
}




**🛠️ Tech Stack**
---

- Java 17+

- Spring Boot (Web, Data JPA, Validation)

- H2 Database (in-memory for development)

- JUnit & Mockito (testing)

- Postman

- Maven (build tool)


## 🧪 Testing

The project was tested using **Postman** to validate the API endpoints.  
Each endpoint was verified for correct request/response behavior, including:

- ✅ Successful request with valid data  
- ❌ Error handling with invalid input  
- 🔑 Authentication & authorization (still in progress)




**✅ What I Learned**
---

- Structuring a Spring Boot application with controllers, services, and repositories.

- Applying input validation and error handling in REST APIs.

- Using Spring Data JPA for database interaction.

- Writing unit & integration tests to ensure code reliability.

- Following clean code principles for readability and maintainability.


## 📑 Project Report
A detailed report about the project design, implementation, and testing is available in the [`docs/Project_Report.pdf`]([vacation/docs/Project_Report.pdf](https://drive.google.com/file/d/1IeNMvJMdoVypuOlzmKJFKgTjPCXlLPQu/view?usp=drive_link)).



**▶️ Running the Project**
---

Clone this repository:

bash

git clone https://github.com/EddieMjiyakho/Vacation_API.git

Navigate into the project folder:

bash

cd vacation-api

Run the application:

bash

./mvnw spring-boot:run

Access the API at:

http://localhost:8080/api




**📌 Future Improvements**
---

- 🔐 Add authentication & role-based access (employee vs manager).

- 🗃️ Connect to a persistent database (PostgreSQL/MySQL).

- 📊 Add analytics (e.g., vacation trends per department).

- 🌐 Build a simple frontend UI for user interaction.
