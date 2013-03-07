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
package fr.ybonnel.simpleweb.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.reflections.Reflections;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Collection;

public class SimpleEntityManager<T, I extends Serializable> {

    private Class<T> entityClass;

    public SimpleEntityManager(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    private static String cfgPath = "fr/ybonnel/simpleweb/model/hibernate.cfg.xml";

    public static void setCfgPath(String cfgPath) {
        SimpleEntityManager.cfgPath = cfgPath;
    }

    private static String entitiesPackage = null;

    public static void setEntitiesPackage(String newEntitiesPackge) {
        entitiesPackage = newEntitiesPackge;
    }

    private static SessionFactory sessionFactory = null;

    public static boolean hasEntities() {
        return !getAnnotatedClasses().isEmpty();
    }


    private static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (SimpleEntityManager.class) {
                if (sessionFactory == null) {

                    Configuration configuration = new Configuration();
                    configuration.configure(cfgPath);
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

    private static ThreadLocal<Session> currentSessions = new ThreadLocal<>();

    public static Session openSession() {
        if (getCurrentSession() != null)  {
            throw new IllegalStateException("There is already a session for the current thread");
        }
        Session session = getSessionFactory().openSession();
        currentSessions.set(session);
        return session;
    }

    public static void closeSession() {
        Session session = currentSessions.get();
        if (session == null) {
            throw new IllegalStateException("There is no session for the current thread");
        }
        session.close();
        currentSessions.remove();
    }

    public static Session getCurrentSession() {
        return currentSessions.get();
    }

    private static Collection<Class<?>> annotatedClasses = null;

    private static Collection<Class<?>> getAnnotatedClasses() {
        if (annotatedClasses == null) {
            Reflections reflections = entitiesPackage == null ? new Reflections("") : new Reflections(entitiesPackage);
            annotatedClasses = reflections.getTypesAnnotatedWith(Entity.class);
        }
        return annotatedClasses;
    }

    public void save(T entity) {
        getCurrentSession().save(entity);
    }

    public void update(T entity) {
        getCurrentSession().update(entity);
    }

    public T getById(I id) {
        return (T) getCurrentSession().get(entityClass, id);
    }

    public Collection<T> getAll() {
        return getCurrentSession().createCriteria(entityClass).list();
    }

    public void delete(I id) {
        getCurrentSession().delete(getCurrentSession().get(entityClass, id));
    }
}
