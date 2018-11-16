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
