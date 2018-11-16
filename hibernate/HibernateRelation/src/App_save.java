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
