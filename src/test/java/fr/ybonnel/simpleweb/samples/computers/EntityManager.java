/*
 * Copyright 2013- Yan Bonnel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ybonnel.simpleweb.samples.computers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Collection;

public class EntityManager<T, I extends Serializable> {

    private Class<T> entityClass;

    protected EntityManager(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private static SessionFactory sessionFactory = null;

    protected static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (EntityManager.class) {
                if (sessionFactory == null) {
                    Configuration configuration = new Configuration();
                    configuration.configure("fr/ybonnel/simpleweb/samples/computerscfg/hibernate.cfg.xml");
                    for (Class<?> entityClass : getAnnotatedClasses()) {
                        configuration.addAnnotatedClass(entityClass);
                    }
                    ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
                    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                }
            }
        }
        return sessionFactory;
    }

    private static Collection<Class<?>> getAnnotatedClasses() {

        Reflections reflections = new Reflections("fr.ybonnel.simpleweb.samples.computers");
        return reflections.getTypesAnnotatedWith(Entity.class);
    }

    public void save(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.save(entity);
        transaction.commit();
        session.close();
    }

    public void update(T entity) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
        session.close();
    }

    public T getById(I id) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        T entity = (T) session.get(entityClass, id);
        transaction.commit();
        session.close();
        return entity;
    }

    public Collection<T> getAll() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Collection<T> entities = session.createCriteria(entityClass).list();
        transaction.commit();
        session.close();
        return entities;
    }

    public void delete(I id) {
        Session session = getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        session.delete(session.get(entityClass, id));
        transaction.commit();
        session.close();
    }
}
