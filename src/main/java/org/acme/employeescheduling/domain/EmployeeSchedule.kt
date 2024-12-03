package org.acme.employeescheduling.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore
import ai.timefold.solver.core.api.solver.SolverStatus

@PlanningSolution
class EmployeeSchedule {
    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private var employees: MutableList<Employee?>? = null

    @PlanningEntityCollectionProperty
    private var shifts: MutableList<Shift?>? = null

    @PlanningScore
    private var score: HardSoftBigDecimalScore? = null

    private var solverStatus: SolverStatus? = null

    // No-arg constructor required for Timefold
    constructor()

    constructor(employees: MutableList<Employee?>?, shifts: MutableList<Shift?>?) {
        this.employees = employees
        this.shifts = shifts
    }

    constructor(score: HardSoftBigDecimalScore?, solverStatus: SolverStatus?) {
        this.score = score
        this.solverStatus = solverStatus
    }

//    fun getEmployees(): MutableList<Employee?>? {
//        return employees
//    }

    fun setEmployees(employees: MutableList<Employee?>?) {
        this.employees = employees
    }

    fun getShifts(): MutableList<Shift?>? {
        return shifts
    }

    fun setShifts(shifts: MutableList<Shift?>?) {
        this.shifts = shifts
    }

    fun getScore(): HardSoftBigDecimalScore? {
        return score
    }

    fun setScore(score: HardSoftBigDecimalScore?) {
        this.score = score
    }

    fun getSolverStatus(): SolverStatus? {
        return solverStatus
    }

    fun setSolverStatus(solverStatus: SolverStatus?) {
        this.solverStatus = solverStatus
    }
}
