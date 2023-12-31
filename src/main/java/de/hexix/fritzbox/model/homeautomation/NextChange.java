/**
 * A Java API for managing FritzBox HomeAutomation
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.hexix.fritzbox.model.homeautomation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "nextchange")
public class NextChange {

    @Element(name = "endperiod", required = false)
    private int endperiod;

    @Element(name = "tchange", required = false)
    private int tchange;

    public int getEndperiod() {
        return endperiod;
    }

    public void setEndperiod(int endperiod) {
        this.endperiod = endperiod;
    }

    public int getTchange() {
        return tchange;
    }

    public void setTchange(int tchange) {
        this.tchange = tchange;
    }

    @Override
    public String toString() {
        return "NextChange [endperiod=" + endperiod + ", tchange=" + tchange + "]";
    }
}
