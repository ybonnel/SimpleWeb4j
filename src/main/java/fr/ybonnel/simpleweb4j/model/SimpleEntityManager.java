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
package fr.ybonnel.simpleweb4j.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Simple entityManager from SimpleWeb4j.
 * Use :
 * {@code SimpleEntityManager<Computer, Long> simpleEntityManager = new SimpleEntityManager<>(Computer.class);
 * }
 * @param <T> The entity to manage.
 * @param <I> The id type.
 */
public class SimpleEntityManager<T, I extends Serializable> {

    /**
     * Class of the entity.
     */
    private Class<T> entityClass;

    /**
     * Constructor.
     * @param entityClass class of entity.
     */
    public SimpleEntityManager(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Path to hibernate config file.
     */
    private static String cfgPath = "fr/ybonnel/simpleweb4j/model/hibernate.cfg.xml";

    /**
     * Change the path to hibernate config file.
     * @param newCfgPath new path to hibernate config file.
     */
    public static void setCfgPath(String newCfgPath) {
        cfgPath = newCfgPath;
    }

    /**
     * List of entities.
     */
    private static Collection<Class<?>> entitiesClasses = null;

    /**
     * Change the list of entities classes.
     * @param newEntitiesClasses new list of entities classes.
     */
    public static void setEntitiesClasses(Collection<Class<?>> newEntitiesClasses) {
        entitiesClasses = newEntitiesClasses;
    }

    /**
     * Method used to know if the current application have entities to manage.
     * @return true is there's entities to manage.
     */
    public static boolean hasEntities() {
        return !getAnnotatedClasses().isEmpty();
    }

    /**
     * Helper to have lazy initialize of SessionFactory.
     */
    private static class SessionFactoryHelper {
        /**
         * Session factory.
         */
        //CHECKSTYLE:OFF
        public static SessionFactory sessionFactory = init();
        //CHECKSTYLE:ON

        /**
         * Initialize of Session factory.
         * @return the session factory initialized.
         */
        private static SessionFactory init() {
            Configuration configuration = new Configuration();
            configuration.configure(cfgPath);
            for (Class<?> entityClass : getAnnotatedClasses()) {
                configuration.addAnnotatedClass(entityClass);
            }
            ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
                    configuration.getProperties()).buildServiceRegistry();
            return configuration.buildSessionFactory(serviceRegistry);
        }

        /**
         * Reset the session factory, used for tests.
         */
        protected static void reset() {
            sessionFactory = init();
        }
    }

    /**
     * Save state of annotated classes to allow changes for tests.
     */
    private static Collection<Class<?>> oldAnnotatedClasses = null;

    /**
     * Get the session factory.
     * @return session factory.
     */
    protected static SessionFactory getSessionFactory() {
        if (oldAnnotatedClasses != null
                && oldAnnotatedClasses != getAnnotatedClasses()
                && !Arrays.equals(oldAnnotatedClasses.toArray(), getAnnotatedClasses().toArray())) {
            SessionFactoryHelper.reset();
        }
        oldAnnotatedClasses = getAnnotatedClasses();
        return SessionFactoryHelper.sessionFactory;
    }

    /**
     * Opened sessions.
     */
    private static ThreadLocal<Session> currentSessions = new ThreadLocal<>();

    /**
     * Open a session (don't use it directly, the session is automatically open by SimpleWeb4j).
     * @return the opened session.
     */
    public static Session openSession() {
        if (getCurrentSession() != null)  {
            throw new IllegalStateException("There is already a session for the current thread");
        }
        Session session = getSessionFactory().openSession();
        currentSessions.set(session);
        return session;
    }

    /**
     * Close the current session (don't use it directly, the session is automatically close by SimpleWeb4j).
     */
    public static void closeSession() {
        Session session = currentSessions.get();
        if (session == null) {
            throw new IllegalStateException("There is no session for the current thread");
        }
        session.close();
        currentSessions.remove();
    }

    /**
     * Get the current session.
     * @return the current session.
     */
    public static Session getCurrentSession() {
        return currentSessions.get();
    }


    /**
     * Get the annotated classes (entities).
     *
     * @return the list of annotated classes.
     */
    private static Collection<Class<?>> getAnnotatedClasses() {
        if (entitiesClasses == null) {
            entitiesClasses = new ArrayList<>();
        }
        return entitiesClasses;
    }

    /**
     * Save an entity.
     * @param entity the entity to save.
     */
    public void save(T entity) {
        getCurrentSession().save(entity);
    }

    /**
     * Update an entity.
     * @param entity entity to update.
     */
    public void update(T entity) {
        getCurrentSession().update(entity);
    }

    /**
     * Get an entity by the id.
     * @param id id of the entity.
     * @return the entity if found, null otherwise.
     */
    @SuppressWarnings("unchecked")
    public T getById(I id) {
        return (T) getCurrentSession().get(entityClass, id);
    }

    /**
     * Get all entities.
     * @return the list of all entities.
     */
    @SuppressWarnings("unchecked")
    public Collection<T> getAll() {
        return getCurrentSession().createCriteria(entityClass).list();
    }

    /**
     * Delete an entity.
     * @param id id of the entity.
     */
    public void delete(I id) {
        getCurrentSession().delete(getCurrentSession().get(entityClass, id));
    }
}
