package de.bund.bva.isyfact.ueberwachung.service.loadbalancer;

import java.io.File;
import java.io.IOException;
import java.io.Serial;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import de.bund.bva.isyfact.logging.IsyLogger;
import de.bund.bva.isyfact.logging.IsyLoggerFactory;
import de.bund.bva.isyfact.logging.LogKategorie;
import de.bund.bva.isyfact.ueberwachung.common.konstanten.EreignisSchluessel;

/**
 * Servlet for controlling the load balancing of a web application. The load balancer
 * can periodically invoke the servlet's URL. The servlet returns HTTP OK if the
 * IsAlive file is found. Otherwise, HTTP FORBIDDEN is reported to the calling
 * load balancer. In that case, the load balancer will no longer route requests
 * to the web application.
 * If required, the path to the IsAlive file can be specified using the init
 * parameter {@link #PARAM_IS_ALIVE_FILE_LOCATION}. If the parameter is not set,
 * the default value {@link #DEFAULT_IS_ALIVE_FILE_LOCATION} is used.
 */
public class LoadbalancerServlet extends HttpServlet {

    /** UID of the class. */
    @Serial
    private static final long serialVersionUID = 7248576003928677600L;

    /** Logger of the class. */
    private static final IsyLogger LOG = IsyLoggerFactory.getLogger(LoadbalancerServlet.class);

    /** Parameter name for the path to the IsAlive file. */
    private static final String PARAM_IS_ALIVE_FILE_LOCATION = "isAliveFileLocation";

    /** Default location of the IsAlive file. */
    private static final String DEFAULT_IS_ALIVE_FILE_LOCATION = "/WEB-INF/classes/config/isAlive";

    /**
     * Reference for the IsAlive file.
     */
    private static File isAliveFile;

    /**
     * Initializing the Servlet.
     */
    @Override
    public void init() {
        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "Initialisiere Loadbalancer-Servlet.");

        String isAliveFileLocation = getInitParameter(PARAM_IS_ALIVE_FILE_LOCATION);
        if (isAliveFileLocation == null) {
            LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001,
                "Position der IsAliveDatei nicht konfiguriert. Verwende Standard-Einstellung: {}",
                DEFAULT_IS_ALIVE_FILE_LOCATION);
            isAliveFileLocation = DEFAULT_IS_ALIVE_FILE_LOCATION;
        }
        String realPath = getServletContext().getRealPath(isAliveFileLocation);
        isAliveFile = realPath != null
                ? new File(realPath)
                : new File(isAliveFileLocation);

        LOG.info(LogKategorie.JOURNAL, EreignisSchluessel.PLUEB00001, "IsAlive-Datei {} konfiguriert.",
            isAliveFile.getAbsolutePath());
    }

    /**
     * Handles a GET request. Checks whether the IsAlive file exists and, if so,
     * returns HTTP OK.
     * Otherwise, HTTP FORBIDDEN is returned.
     *
     * @param req
     *            The HttpServletRequest sent to the load balancer servlet.
     * @param resp
     *            The response of the load balancer servlet.
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (isAliveFile.exists()) {
            LOG.debug("IsAlive-Datei gefunden, sende HTTP OK.");
            resp.setStatus(HttpServletResponse.SC_OK);
            try {
                resp.getWriter().write("<html><body><center>IS ALIVE!</center></body></html>");
            } catch (IOException _) {
                LOG.error(
                        EreignisSchluessel.IS_ALIVE_EXISTIERT_IO_EXCEPTION,
                        "IsyAlive-Datei {} existiert, fehler bei schreiben der Antwort in output-stream", isAliveFile.getAbsolutePath());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            LOG.error(
                    EreignisSchluessel.IS_ALIVE_EXISTIERT_NICHT,
                "IsAlive-Datei {} existiert nicht, sende HTTP FORBIDDEN.", isAliveFile.getAbsolutePath());
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
