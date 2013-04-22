/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.projekt.pv168_semestralny_projekt;

/**
 *
 * @author Martin Otahal
 */
public class AppException extends Throwable
{

    public AppException(String message, Throwable cause) 
    {
        super(message, cause);
    }

    public AppException(String message)
    {
        super(message);
    }
}
