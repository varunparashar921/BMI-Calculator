package pl.edu.pwr.swim.marcinkiewicz.szymon.bmi;

/**
 * Created by Szymon on 2017-03-14.
 */
public class BMICalcForPound implements IBMICalc{
    public static float minHeight = 19.68f;
    public static float maxHeight = 98.42f;
    public static float minMass = 22f;
    public static float maxMass = 440f;

    @Override
    public float countBMI(float mass, float height) {
        if(isValidMass(mass) && isValidHeight(height))
            return mass / (height * height) * 703;
        else
            throw new IllegalArgumentException();
    }

    @Override
    public boolean isValidMass(float mass) {
        return minMass <= mass && mass <= maxMass;
    }

    @Override
    public boolean isValidHeight(float height) {
        return minHeight <= height && height <= maxHeight;
    }
}