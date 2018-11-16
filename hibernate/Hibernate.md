# 初识Hibernate

### 创建Hibernate Project

1. ##### 在 Intellij Utimate IDEA 中新建 Hibernate 项目

   ![1](C:\Users\Candice\iCloudDrive\Desktop\DataBase\实验\week9\pic\1.PNG)

2. ##### IDEA 会自动生成 hibernate.cfg.xml 配置文件

   ~~~xml
   <?xml version='1.0' encoding='utf-8'?>
   <!DOCTYPE hibernate-configuration PUBLIC
           "-//Hibernate/Hibernate Configuration DTD//EN"
           "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
   <hibernate-configuration>
       <session-factory>
           <!-- 这个属性使 Hibernate 应用为被选择的数据库生成适当的SQL -->
           <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
           <!-- JDBC 驱动程序类 -->
           <property name="connection.driver_class">org.postgresql.Driver</property>
           <!-- 数据库实例的 JDBC URL -->
           <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/postgres</property>
           <!-- 连接数据库的用户名 -->
           <property name="hibernate.connection.username">postgres</property>
           <!-- 连接数据库的密码 -->
           <property name="hibernate.connection.password">password</property>
   
           <!-- Set "true" to show SQL statements -->
           <property name="hibernate.show_sql">true</property>
   
           <!-- mapping class using annotation -->
           <mapping class="entities.Employee"></mapping>
   
       </session-factory>
   </hibernate-configuration>
   ~~~

3. ##### 在数据库中创建一个EMPLOYEE表格

   ~~~sql
   create table EMPLOYEE (
       id SERIAL,
       first_name VARCHAR(20) default NULL,
       last_name  VARCHAR(20) default NULL,
       salary     INT  default NULL,
       PRIMARY KEY (id)
   );
   ~~~

4. ##### 利用 IDEA 自动生成对应的 EMPLOYEE 对象类

   - 首先连接到数据库

   ![2](C:\Users\Candice\iCloudDrive\Desktop\DataBase\实验\week9\pic\2.PNG)

   - 利用 Persistence 下的 Generate Persistence Mapping 构造对象类

     ![3](C:\Users\Candice\iCloudDrive\Desktop\DataBase\实验\week9\pic\3.PNG)

   - 以下是自动生成的对象类，需要注意的是构造函数是不会自动生成的，需要自己编写。

   ~~~java
   package entities;
   
   import javax.persistence.*;
   import java.util.Objects;
   
   @Entity
   public class Employee {
       private int id;
       private String firstName;
       private String lastName;
       private Integer salary;
   
       public Employee(){}
       public Employee(String fname, String lname, int salary) {
           this.firstName = fname;
           this.lastName = lname;
           this.salary = salary;
       }
   
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       @Column(name = "id", nullable = false)
       public int getId() {
           return id;
       }
   
       public void setId(int id) {
           this.id = id;
       }
   
       @Basic
       @Column(name = "first_name", nullable = true, length = 20)
       public String getFirstName() {
           return firstName;
       }
   
       public void setFirstName(String firstName) {
           this.firstName = firstName;
       }
   
       @Basic
       @Column(name = "last_name", nullable = true, length = 20)
       public String getLastName() {
           return lastName;
       }
   
       public void setLastName(String lastName) {
           this.lastName = lastName;
       }
   
       @Basic
       @Column(name = "salary", nullable = true)
       public Integer getSalary() {
           return salary;
       }
   
       public void setSalary(Integer salary) {
           this.salary = salary;
       }
   
       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           Employee employee = (Employee) o;
           return id == employee.id &&
                   Objects.equals(firstName, employee.firstName) &&
                   Objects.equals(lastName, employee.lastName) &&
                   Objects.equals(salary, employee.salary);
       }
   
       @Override
       public int hashCode() {
           return Objects.hash(id, firstName, lastName, salary);
       }
   }
   
   ~~~

5. 编写函数测试

   ~~~java
   import java.util.List;
   import java.util.Iterator;
   
   import entities.Employee;
   import org.hibernate.HibernateException;
   import org.hibernate.Session;
   import org.hibernate.Transaction;
   import org.hibernate.SessionFactory;
   import org.hibernate.cfg.Configuration;
   
   public class ManageEmployee {
       private static SessionFactory factory;
       public static void main(String[] args) {
           try{
               factory = new Configuration().configure().buildSessionFactory();
           }catch (Throwable ex) {
               System.err.println("Failed to create sessionFactory object." + ex);
               throw new ExceptionInInitializerError(ex);
           }
           ManageEmployee ME = new ManageEmployee();
   
           /* Add few employee records in database */
           Integer empID1 = ME.addEmployee("Zara", "Ali", 1000);
           Integer empID2 = ME.addEmployee("Daisy", "Das", 5000);
           Integer empID3 = ME.addEmployee("John", "Paul", 10000);
   
           /* List down all the employees */
           ME.listEmployees();
   
           /* Update employee's records */
           ME.updateEmployee(empID1, 5000);
   
           /* Delete an employee from the database */
           ME.deleteEmployee(empID2);
   
           /* List down new list of the employees */
           ME.listEmployees();
       }
       /* Method to CREATE an employee in the database */
       public Integer addEmployee(String fname, String lname, int salary){
           Session session = factory.openSession();
           Transaction tx = null;
           Integer employeeID = null;
           try{
               tx = session.beginTransaction();
               Employee employee = new Employee(fname, lname, salary);
               employeeID = (Integer) session.save(employee);
               tx.commit();
           }catch (HibernateException e) {
               if (tx!=null) tx.rollback();
               e.printStackTrace();
           }finally {
               session.close();
           }
           return employeeID;
       }
       /* Method to  READ all the employees */
       public void listEmployees( ){
           Session session = factory.openSession();
           Transaction tx = null;
           try{
               tx = session.beginTransaction();
               List employees = session.createQuery("FROM Employee").list();
               for (Iterator iterator =
                    employees.iterator(); iterator.hasNext();){
                   Employee employee = (Employee) iterator.next();
                   System.out.print("First Name: " + employee.getFirstName());
                   System.out.print("  Last Name: " + employee.getLastName());
                   System.out.println("  Salary: " + employee.getSalary());
               }
               tx.commit();
           }catch (HibernateException e) {
               if (tx!=null) tx.rollback();
               e.printStackTrace();
           }finally {
               session.close();
           }
       }
       /* Method to UPDATE salary for an employee */
       public void updateEmployee(Integer EmployeeID, int salary ){
           Session session = factory.openSession();
           Transaction tx = null;
           try{
               tx = session.beginTransaction();
               Employee employee =
                       (Employee)session.get(Employee.class, EmployeeID);
               employee.setSalary( salary );
               session.update(employee);
               tx.commit();
           }catch (HibernateException e) {
               if (tx!=null) tx.rollback();
               e.printStackTrace();
           }finally {
               session.close();
           }
       }
       /* Method to DELETE an employee from the records */
       public void deleteEmployee(Integer EmployeeID){
           Session session = factory.openSession();
           Transaction tx = null;
           try{
               tx = session.beginTransaction();
               Employee employee =
                       (Employee)session.get(Employee.class, EmployeeID);
               session.delete(employee);
               tx.commit();
           }catch (HibernateException e) {
               if (tx!=null) tx.rollback();
               e.printStackTrace();
           }finally {
               session.close();
           }
       }
   }
   
   ~~~

   测试结果：

   ![4](C:\Users\Candice\iCloudDrive\Desktop\DataBase\实验\week9\pic\4.PNG)



### 设置表间关联关系

设计student有多台电脑，是一个1:N的关系

**student类**

~~~java
package entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Student {
    private int rollno;
    private String sname;
    private int marks;
    private Set<Laptop> laptops;

    @OneToMany(targetEntity=Laptop.class, mappedBy="studs", fetch=FetchType.EAGER)
    public Set<Laptop> getLaptops() {
        return laptops;
    }

    public void setLaptops(Set<Laptop> laptops) {
        this.laptops = laptops;
    }

    @Id
    @Column(name = "rollno", nullable = false)
    public int getRollno() {
        return rollno;
    }

    public void setRollno(int rollno) {
        this.rollno = rollno;
    }

    @Basic
    @Column(name = "sname", nullable = false, length = 20)
    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    @Basic
    @Column(name = "marks", nullable = true)
    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return rollno == student.rollno &&
                Objects.equals(sname, student.sname) &&
                Objects.equals(marks, student.marks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollno, sname, marks);
    }
}

~~~

**laptop类**

~~~java
package entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Laptop {
    private int lid;
    private String lname;
    private Student studs;

    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY, optional=true)
    @JoinColumn(name="studsid", nullable=true)
    public Student getStuds() {
        return studs;
    }

    public void setStuds(Student studs) {
        this.studs = studs;
    }

    @Id
//    @GenericGenerator(name="generator",strategy="uuid")
//    @GeneratedValue(generator="generator")
    @Column(name = "lid", nullable = false)
    public int getLid() {
        return lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    @Basic
    @Column(name = "lname", nullable = false, length = 20)
    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Laptop laptop = (Laptop) o;
        return lid == laptop.lid &&
                Objects.equals(lname, laptop.lname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lid, lname);
    }
}

~~~

**配置文件**

~~~java
<?xml version='1.0' encoding='utf-8'?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- 这个属性使 Hibernate 应用为被选择的数据库生成适当的SQL -->
        <property name="hibernate.dialect">org.hibernate.dialect.PostgreSQLDialect</property>
        <!-- JDBC 驱动程序类 -->
        <property name="connection.driver_class">org.postgresql.Driver</property>
        <!-- 数据库实例的 JDBC URL -->
        <property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/postgres</property>
        <!-- 连接数据库的用户名 -->
        <property name="hibernate.connection.username">postgres</property>
        <!-- 连接数据库的密码 -->
        <property name="hibernate.connection.password">password</property>

        <!-- Set "true" to show SQL statements -->
        <property name="hibernate.show_sql">true</property>

        <!-- 自动创建表 -->
        <property name="hibernate.hbm2ddl.auto">update</property>

        <mapping class="entities.Student"/>
        <mapping class="entities.Laptop"/>
    </session-factory>
</hibernate-configuration>
~~~

**测试函数**

~~~java
import entities.Laptop;
import entities.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;


public class App_save {
    public static SessionFactory factory = new Configuration().configure().buildSessionFactory();

    @Test
    public void save(){
        Session session = factory.openSession();
        session.beginTransaction();

        Student stu1 = new Student();
        stu1.setRollno(1);
        stu1.setSname("Alice");
        stu1.setMarks(100);

        Laptop lap1 = new Laptop();
        lap1.setLid(1);
        lap1.setLname("lap1");
        lap1.setStuds(stu1);
        Laptop lap2 = new Laptop();
        lap2.setLid(2);
        lap2.setLname("lap2");
        lap2.setStuds(stu1);

        session.save(stu1);
        session.save(lap1);
        session.save(lap2);

        session.getTransaction().commit();
        session.close();
    }
}
~~~

**运行结果**

![result1](C:\Users\Candice\Desktop\DBMS\hibernate\result1.PNG)

![result2](C:\Users\Candice\Desktop\DBMS\hibernate\result2.PNG)

### 参考案例

- https://www.w3cschool.cn/hibernate/gfhs1iep.html
- https://www.jianshu.com/p/50e0a7a28b53
- https://www.cnblogs.com/whgk/p/6074930.html
- https://blog.csdn.net/carl4254/article/details/69664004

### 学习教程

- https://goo.gl/UKAdaq

- https://www.w3cschool.cn/hibernate/


