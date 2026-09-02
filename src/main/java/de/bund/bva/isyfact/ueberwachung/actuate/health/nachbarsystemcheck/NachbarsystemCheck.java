package de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck;

import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.Nachbarsystem;
import de.bund.bva.isyfact.ueberwachung.actuate.health.nachbarsystemcheck.model.NachbarsystemHealth;

/**
 * Health check of a neighbouring system.
 * @deprecated Deprecated since IF6
 */
@FunctionalInterface
@Deprecated
public interface NachbarsystemCheck {

    /**
     * Health check of a neighbouring system.
     * The health endpoint of the neighbour is queried and the
     * output is capsuled into a NachbarsystemHealth-object.
     *
     * @param nachbarsystem neighbouring system
     * @return return value of the health check
     */
    NachbarsystemHealth checkNachbarsystem(Nachbarsystem nachbarsystem);

}
