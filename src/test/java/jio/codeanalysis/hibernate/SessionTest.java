package jio.codeanalysis.hibernate;

import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaType;
import jio.codeanalysis.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SessionTest {

    @Before
    public void clearData() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        clearAll(session, "from JavaType");
        clearAll(session, "from JavaMethod");
        tx.commit();
        session.close();
    }

    private void clearAll(Session session, String constraint) {
        Query query = session.createQuery("delete " + constraint);
        query.executeUpdate();
    }

    @Test
    @Ignore
    public void testCreate() {
        JavaType javaType = new JavaType();
        javaType.setQualifiedName("jio.codeanalysis.java.model.JavaType");
        javaType.setTypeName("JavaType");

        JavaMethod javaMethod = new JavaMethod();
        javaMethod.setQuilifiedName("jio.codeanalysis.java.model.JavaType.method");
        javaMethod.setMethodName("method");

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(javaType);
        session.save(javaMethod);
        session.getTransaction().commit();
        session.close();
    }
}
