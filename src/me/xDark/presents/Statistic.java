package me.xDark.presents;

import java.util.Date;
import java.util.HashSet;

import org.bukkit.Location;

public class Statistic {
	private HashSet<Location> foundPresents;
	private boolean foundAll;

	public int timesFound;

	private boolean editMode;

	public Date findDate;

	public Statistic(boolean foundAll, HashSet<Location> presents, int timesFound, Date findDate) {
		this.foundAll = foundAll;
		editMode = false;
		foundPresents = presents;
		this.timesFound = timesFound;
		this.findDate = findDate;
	}

	public boolean hasPassed(Date date, int days) {
		if (findDate == null)
			return true;
		if (findDate.getDay() - days == 0)
			return true;
		return false;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean editMode() {
		return editMode;
	}

	public HashSet<Location> getFoundPresents() {
		return foundPresents;
	}

	public boolean foundAll() {
		return foundAll;
	}

	public void setFoundAll(boolean foundAll) {
		this.foundAll = foundAll;
	}

}
