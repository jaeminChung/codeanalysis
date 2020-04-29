package jio.codeanalysis.util;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    private static final EntityManager entityManager = createEntityManager();

    private static EntityManager createEntityManager() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            EntityManagerFactory factory = new EntityManagerFactoryImpl(
                    PersistenceUnitTransactionType.RESOURCE_LOCAL, true, null, configuration, serviceRegistry, null);

            return factory.createEntityManager();

        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            ex.printStackTrace();

            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }
    
    public static void close() {
    	entityManager.close();
    }
}