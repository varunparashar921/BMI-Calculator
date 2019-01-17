package pl.edu.pwr.swim.marcinkiewicz.szymon.bmi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by Szymon on 2017-03-14.
 */
public class BMICalculatorTest {

    //Tests for kg
    @Test
    public void massUnderZeroIsInvalidForKg() throws Exception {
        //GIVEN
        float testMass = -1;
        //WHEN
        BMICalcForKg bmiCalc = new BMICalcForKg();
        //THEN
        assertFalse(bmiCalc.isValidMass(testMass));
    }

    @Test
    public void heightUnderZeroIsInvalidForKg() throws Exception{
        //GIVEN
        float testHeight = -1;
        //WHEN
        BMICalcForKg bmiCalc = new BMICalcForKg();
        //THEN
        assertFalse(bmiCalc.isValidHeight(testHeight));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countBMIWithExceptionForKg() throws Exception{
        //GIVEN
        float testHeight = -1;
        float testMass = -1;
        //WHEN
        BMICalcForKg bmiCalc = new BMICalcForKg();
        //THEN
        bmiCalc.countBMI(testMass, testHeight);
    }

    //Tests for pound
    @Test
    public void massUnderZeroIsInvalidForPound() throws Exception {
        //GIVEN
        float testMass = -1;
        //WHEN
        BMICalcForPound bmiCalc = new BMICalcForPound();
        //THEN
        assertFalse(bmiCalc.isValidMass(testMass));
    }

    @Test
    public void heightUnderZeroIsInvalidForPound() throws Exception{
        //GIVEN
        float testHeight = -1;
        //WHEN
        BMICalcForPound bmiCalc = new BMICalcForPound();
        //THEN
        assertFalse(bmiCalc.isValidHeight(testHeight));
    }

    @Test(expected = IllegalArgumentException.class)
    public void countBMIWithExceptionForPound() throws Exception{
        //GIVEN
        float testHeight = -1;
        float testMass = -2;
        //WHEN
        BMICalcForPound bmiCalc = new BMICalcForPound();
        //THEN
        bmiCalc.countBMI(testMass, testHeight);
    }
}
