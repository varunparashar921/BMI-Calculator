package pl.edu.pwr.swim.marcinkiewicz.szymon.bmi;

/**
 * Created by Szymon on 2017-03-14.
 */
public interface IBMICalc {
    public float countBMI(float mass, float height);
    public boolean isValidMass(float mass);
    public boolean isValidHeight(float height);
}
