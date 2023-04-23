package itmo.lab8.commands;

import itmo.lab8.server.response.Response;

/**
 * Action interface
 * <p>
 * This interface is used to define the run() method which is used to execute an action.
 *
 * @author kxrxh
 */
public interface Action {

    Response run(String username);
}
