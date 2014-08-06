package org.exoplatform.com.blog.service.impl;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toannh on 7/21/14.
 */
public class BaseService {
  Log log = ExoLogger.getExoLogger(BaseService.class);

  protected static final String REPO = "repository";
  protected static final String WS = "collaboration";

  protected RepositoryService repoService;
  protected SessionProviderService sessionProviderService;

  /**
   * Get Node by Id
   *
   * @param nodetype
   * @param idNode
   * @param value
   * @return Ex: SELECT * FROM <node>  WHERE <node_ID> LIKE <value>
   * @throws Exception
   */
  protected Node getNode(String nodetype, String idNode, String value) throws Exception {
    Session session = getSession();

    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodetype);
    queryBuilder.append(" WHERE ").append(idNode).append(" like '").append(value).append("'");

    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    if (nodes.hasNext()) {
      return nodes.nextNode();
    }
    return null;
  }

  /**
   * @param nodetype
   * @param node_condition
   * @param value
   * @return Ex: SELECT * FROM <node>  WHERE <node_condition> LIKE <value>
   * @throws Exception
   */
  protected List<Node> getNodes(String nodetype, String node_condition, String value) throws Exception {
    List<Node> result = new ArrayList<Node>();
    Session session = getSession();

    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodetype);
    queryBuilder.append(" WHERE ").append(node_condition).append(" like '").append(value).append("'");

    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    while (nodes.hasNext()) {
      result.add(nodes.nextNode());
    }
    return result;
  }

  /**
   * Get session
   *
   * @return
   * @throws Exception
   */
  protected Session getSession() throws Exception {
    ManageableRepository repository = repoService.getRepository(this.REPO);
    SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
    Session session = sessionProvider.getSession(this.WS, repository);

    return session;
  }

  /**
   * Get All node of element
   *
   * @param nodeElement
   * @return
   * @throws Exception
   */
  protected List<Node> getAllNode(String nodeElement) throws Exception {
    Session session = getSession();
    List<Node> rs = new ArrayList<Node>();
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodeElement);
    queryBuilder.append(" ORDER BY exo:dateCreated DESC ");
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    while (nodes.hasNext()) {
      rs.add(nodes.nextNode());
    }
    return rs;
  }

  /**
   * Get All node by node, month
   *
   * @param nodeElement
   * @param firstDayOfMonth
   * @param lastDayOfMonth
   * @return
   * @throws Exception
   */
  protected List<Node> getAllNode(String nodeElement, String firstDayOfMonth, String lastDayOfMonth) throws Exception {
    Session session = getSession();
    List<Node> rs = new ArrayList<Node>();
    StringBuilder queryBuilder = new StringBuilder("SELECT * FROM ").append(nodeElement);
    queryBuilder.append(" WHERE exo:dateCreated >= TIMESTAMP '" + firstDayOfMonth + "' ");
    queryBuilder.append(" AND   exo:dateCreated <= TIMESTAMP '" + lastDayOfMonth + "' ");
    queryBuilder.append(" ORDER BY exo:dateCreated DESC ");
    QueryManager queryManager = session.getWorkspace().getQueryManager();
    Query query = queryManager.createQuery(queryBuilder.toString(), Query.SQL);

    NodeIterator nodes = query.execute().getNodes();
    while (nodes.hasNext()) {
      rs.add(nodes.nextNode());
    }
    return rs;
  }
}
