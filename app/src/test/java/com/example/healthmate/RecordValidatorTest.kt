package com.example.healthmate

import com.example.healthmate.util.FormField
import com.example.healthmate.util.RecordValidator
import com.example.healthmate.util.ValidationResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RecordValidatorTest {

    @Test
    fun validInput_returnsValid() {
        val result = RecordValidator.validate("9 Nov 2026", "7850", "420", "2000")
        assertEquals(ValidationResult.Valid(7850, 420, 2000), result)
    }

    @Test
    fun emptyField_returnsGeneralError() {
        val result = RecordValidator.validate("9 Nov 2026", "", "420", "2000")
        assertEquals(
            ValidationResult.Invalid(FormField.GENERAL, "Please fill out all fields"), result
        )
    }

    @Test
    fun negativeSteps_returnsStepsError() {
        val result = RecordValidator.validate("9 Nov 2026", "-5", "420", "2000")
        assertEquals(ValidationResult.Invalid(FormField.STEPS, "Enter valid steps"), result)
    }

    @Test
    fun nonNumericCalories_returnsCaloriesError() {
        val result = RecordValidator.validate("9 Nov 2026", "100", "abc", "2000")
        assertEquals(ValidationResult.Invalid(FormField.CALORIES, "Enter valid calories"), result)
    }

    @Test
    fun negativeWater_returnsWaterError() {
        val result = RecordValidator.validate("9 Nov 2026", "100", "50", "-1")
        assertEquals(ValidationResult.Invalid(FormField.WATER, "Enter valid water amount"), result)
    }

    @Test
    fun decimalValues_areRoundedToWholeNumbers() {
        val result = RecordValidator.validate("9 Nov 2026", "100", "50.4", "1999.6")
        assertEquals(ValidationResult.Valid(100, 50, 2000), result)
    }

    @Test
    fun goals_mustBePositive() {
        assertEquals(Triple(10000, 2000, 2500), RecordValidator.validateGoals(10000, 2000, 2500))
        assertNull(RecordValidator.validateGoals(0, 2000, 2500))
        assertNull(RecordValidator.validateGoals(10000, null, 2500))
    }
}
