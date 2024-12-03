package org.acme.employeescheduling.solver

import ai.timefold.solver.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners
import ai.timefold.solver.core.api.score.stream.common.LoadBalance
import org.acme.employeescheduling.domain.Employee
import org.acme.employeescheduling.domain.Shift
import java.time.Duration
import java.time.LocalDate
import java.time.chrono.ChronoLocalDateTime
import java.util.function.BiFunction
import java.util.function.BiPredicate
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.ToIntBiFunction
import java.util.function.ToLongBiFunction

class EmployeeSchedulingConstraintProvider2kt : ConstraintProvider {
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint?> {
        return arrayOf<Constraint?>( // Hard constraints
            requiredSkill(constraintFactory),
            noOverlappingShifts(constraintFactory),
            atLeast10HoursBetweenTwoShifts(constraintFactory),
            oneShiftPerDay(constraintFactory),
            unavailableEmployee(constraintFactory),  // Soft constraints
            undesiredDayForEmployee(constraintFactory),
            desiredDayForEmployee(constraintFactory),
            balanceEmployeeShiftAssignments(constraintFactory)
        )
    }

    fun requiredSkill(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<Shift?>(Shift::class.java)
            .filter(Predicate { shift: Shift? ->
                !shift!!.getEmployee()!!.getSkills()!!.contains(shift.getRequiredSkill())
            })
            .penalize<HardSoftBigDecimalScore?>(HardSoftBigDecimalScore.ONE_HARD)
            .asConstraint("Missing required skill")
    }

    fun noOverlappingShifts(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair<Shift?>(
            Shift::class.java, Joiners.equal<Shift?, Employee?>(Function { obj: Shift? -> obj!!.getEmployee() }),
            Joiners.overlapping<Shift?, ChronoLocalDateTime<*>?>(
                Function { obj: Shift? -> obj!!.getStart() },
                Function { obj: Shift? -> obj!!.getEnd() })
        )
            .penalize<HardSoftBigDecimalScore?>(
                HardSoftBigDecimalScore.ONE_HARD,
                ToIntBiFunction { shift1: Shift?, shift2: Shift? -> Companion.getMinuteOverlap(shift1!!, shift2!!) })
            .asConstraint("Overlapping shift")
    }

    fun atLeast10HoursBetweenTwoShifts(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<Shift?>(Shift::class.java)
            .join<Shift?>(
                Shift::class.java,
                Joiners.equal<Shift?, Employee?>(Function { obj: Shift? -> obj!!.getEmployee() }),
                Joiners.lessThanOrEqual<Shift?, Shift?, ChronoLocalDateTime<*>?>(
                    Function { obj: Shift? -> obj!!.getEnd() },
                    Function { obj: Shift? -> obj!!.getStart() })
            )
            .filter(BiPredicate { firstShift: Shift?, secondShift: Shift? ->
                Duration.between(
                    firstShift!!.getEnd(),
                    secondShift!!.getStart()
                ).toHours() < 10
            })
            .penalize<HardSoftBigDecimalScore?>(
                HardSoftBigDecimalScore.ONE_HARD,
                ToIntBiFunction { firstShift: Shift?, secondShift: Shift? ->
                    val breakLength =
                        Duration.between(firstShift!!.getEnd(), secondShift!!.getStart()).toMinutes().toInt()
                    (10 * 60) - breakLength
                })
            .asConstraint("At least 10 hours between 2 shifts")
    }

    fun oneShiftPerDay(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEachUniquePair<Shift?>(
            Shift::class.java,
            Joiners.equal<Shift?, Employee?>(Function { obj: Shift? -> obj!!.getEmployee() }),
            Joiners.equal<Shift?, LocalDate?>(Function { shift: Shift? -> shift!!.getStart()!!.toLocalDate() })
        )
            .penalize<HardSoftBigDecimalScore?>(HardSoftBigDecimalScore.ONE_HARD)
            .asConstraint("Max one shift per day")
    }

    fun unavailableEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<Shift?>(Shift::class.java)
            .join<Employee?>(
                Employee::class.java,
                Joiners.equal<Shift?, Employee?, Employee?>(
                    Function { obj: Shift? -> obj!!.getEmployee() },
                    Function.identity<Employee?>()
                )
            )
            .flattenLast<LocalDate?>(Function { obj: Employee? -> obj!!.getUnavailableDates()!! })
            .filter(BiPredicate { obj: Shift?, date: LocalDate? -> obj!!.isOverlappingWithDate(date) })
            .penalize<HardSoftBigDecimalScore?>(
                HardSoftBigDecimalScore.ONE_HARD,
                ToIntBiFunction { obj: Shift?, date: LocalDate? -> obj!!.getOverlappingDurationInMinutes(date!!) })
            .asConstraint("Unavailable employee")
    }

    fun undesiredDayForEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<Shift?>(Shift::class.java)
            .join<Employee?>(
                Employee::class.java,
                Joiners.equal<Shift?, Employee?, Employee?>(
                    Function { obj: Shift? -> obj!!.getEmployee() },
                    Function.identity<Employee?>()
                )
            )
            .flattenLast<LocalDate?>(Function { obj: Employee? -> obj!!.getUndesiredDates()!! })
            .filter(BiPredicate { obj: Shift?, date: LocalDate? -> obj!!.isOverlappingWithDate(date) })
            .penalize<HardSoftBigDecimalScore?>(
                HardSoftBigDecimalScore.ONE_SOFT,
                ToIntBiFunction { obj: Shift?, date: LocalDate? -> obj!!.getOverlappingDurationInMinutes(date!!) })
            .asConstraint("Undesired day for employee")
    }

    fun desiredDayForEmployee(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory.forEach<Shift?>(Shift::class.java)
            .join<Employee?>(
                Employee::class.java,
                Joiners.equal<Shift?, Employee?, Employee?>(
                    Function { obj: Shift? -> obj!!.getEmployee() },
                    Function.identity<Employee?>()
                )
            )
            .flattenLast<LocalDate?>(Function { obj: Employee? -> obj!!.getDesiredDates()!! })
            .filter(BiPredicate { obj: Shift?, date: LocalDate? -> obj!!.isOverlappingWithDate(date) })
            .reward<HardSoftBigDecimalScore?>(
                HardSoftBigDecimalScore.ONE_SOFT,
                ToIntBiFunction { obj: Shift?, date: LocalDate? -> obj!!.getOverlappingDurationInMinutes(date!!) })
            .asConstraint("Desired day for employee")
    }

    fun balanceEmployeeShiftAssignments(constraintFactory: ConstraintFactory): Constraint {
        val asConstraint = constraintFactory.forEach<Shift?>(Shift::class.java)
            .groupBy(object : Function1<Shift, Employee> {
                override fun invoke(shift: Shift): Employee {
                    return shift.getEmployee()!!
                }
            }, ConstraintCollectors.count())
            .complement(
                Employee::class.java,
                object : Function1<Employee, Int> {
                    override fun invoke(e: Employee): Int {
                        return 0
                    }
                }
            ) // Include all employees which are not assigned to any shift.c
            .groupBy(
                ConstraintCollectors.loadBalance(
                    { employee, shiftCount -> employee },
                    { employee, shiftCount -> shiftCount.toLong() }
                )
            )
            .penalizeBigDecimal<HardSoftBigDecimalScore>(
                HardSoftBigDecimalScore.ONE_SOFT,
                Function { obj: LoadBalance<Employee?>? -> obj!!.unfairness() })
            .asConstraint("Balance employee shift assignments")
        return asConstraint
    }

    companion object {
        private fun getMinuteOverlap(shift1: Shift, shift2: Shift): Int {
            // The overlap of two timeslot occurs in the range common to both timeslots.
            // Both timeslots are active after the higher of their two start times,
            // and before the lower of their two end times.
            val shift1Start = shift1.getStart()
            val shift1End = shift1.getEnd()
            val shift2Start = shift2.getStart()
            val shift2End = shift2.getEnd()
            return Duration.between(
                if ((shift1Start!!.isAfter(shift2Start))) shift1Start else shift2Start,
                if ((shift1End!!.isBefore(shift2End))) shift1End else shift2End
            ).toMinutes().toInt()
        }
    }
}
