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
