package org.acme.employeescheduling.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import java.time.LocalDate

class Employee {
    @PlanningId
    private var name: String? = null
    private var skills: MutableSet<String?>? = null

    private var unavailableDates: MutableSet<LocalDate?>? = null
    private var undesiredDates: MutableSet<LocalDate?>? = null
    private var desiredDates: MutableSet<LocalDate?>? = null

    constructor()

    constructor(
        name: String?,
        skills: MutableSet<String?>?,
        unavailableDates: MutableSet<LocalDate?>?,
        undesiredDates: MutableSet<LocalDate?>?,
        desiredDates: MutableSet<LocalDate?>?
    ) {
        this.name = name
        this.skills = skills
        this.unavailableDates = unavailableDates
        this.undesiredDates = undesiredDates
        this.desiredDates = desiredDates
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getSkills(): MutableSet<String?>? {
        return skills
    }

    fun setSkills(skills: MutableSet<String?>?) {
        this.skills = skills
    }

    fun getUnavailableDates(): MutableSet<LocalDate?>? {
        return unavailableDates
    }

    fun setUnavailableDates(unavailableDates: MutableSet<LocalDate?>?) {
        this.unavailableDates = unavailableDates
    }

    fun getUndesiredDates(): MutableSet<LocalDate?>? {
        return undesiredDates
    }

    fun setUndesiredDates(undesiredDates: MutableSet<LocalDate?>?) {
        this.undesiredDates = undesiredDates
    }

    fun getDesiredDates(): MutableSet<LocalDate?>? {
        return desiredDates
    }

    fun setDesiredDates(desiredDates: MutableSet<LocalDate?>?) {
        this.desiredDates = desiredDates
    }

    override fun toString(): String {
        return name!!
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Employee) {
            return false
        }
        return getName() == o.getName()
    }

    override fun hashCode(): Int {
        return getName().hashCode()
    }
}
