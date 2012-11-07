/*
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.jdk8;

import static javax.time.calendrical.LocalDateTimeField.DAY_OF_MONTH;
import static javax.time.calendrical.LocalDateTimeField.EPOCH_DAY;
import static javax.time.calendrical.LocalDateTimeField.ERA;
import static javax.time.calendrical.LocalDateTimeField.MONTH_OF_YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR;
import static javax.time.calendrical.LocalDateTimeField.YEAR_OF_ERA;

import java.util.Objects;

import javax.time.LocalTime;
import javax.time.calendrical.DateTime;
import javax.time.calendrical.PeriodUnit;
import javax.time.chrono.ChronoLocalDate;
import javax.time.chrono.ChronoLocalDateTime;
import javax.time.chrono.Chrono;
import javax.time.chrono.Era;
import javax.time.format.CalendricalFormatter;

/**
 * A temporary class providing implementations that will become default interface
 * methods once integrated into JDK 8.
 */
public abstract class DefaultInterfaceChronoLocalDate<C extends Chrono<C>>
        extends DefaultInterfaceDateTime
        implements ChronoLocalDate<C> {

    @Override
    public Era<C> getEra() {
        return getChrono().eraOf(get(ERA));
    }

    @Override
    public boolean isLeapYear() {
        return getChrono().isLeapYear(getLong(YEAR));
    }

    @Override
    public int lengthOfYear() {
        return (isLeapYear() ? 366 : 365);
    }

    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDate<C> with(WithAdjuster adjuster) {
        return (ChronoLocalDate<C>) super.with(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDate<C> plus(PlusAdjuster adjuster) {
        return (ChronoLocalDate<C>) super.plus(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDate<C> minus(MinusAdjuster adjuster) {
        return (ChronoLocalDate<C>) super.minus(adjuster);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ChronoLocalDate<C> minus(long amountToSubtract, PeriodUnit unit) {
        return (ChronoLocalDate<C>) super.minus(amountToSubtract, unit);
    }

    //-------------------------------------------------------------------------
    @Override
    public DateTime doWithAdjustment(DateTime calendrical) {
        return calendrical.with(EPOCH_DAY, getLong(EPOCH_DAY));
    }

    @Override
    public ChronoLocalDateTime<C> atTime(LocalTime localTime) {
        return Chrono.dateTime(this, localTime);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R extract(Class<R> type) {
        if (type == Chrono.class) {
            return (R) getChrono();
        }
        return null;
    }

    //-------------------------------------------------------------------------
    @Override
    public boolean isAfter(ChronoLocalDate<?> other) {
        return this.getLong(EPOCH_DAY) > other.getLong(EPOCH_DAY);
    }

    @Override
    public boolean isBefore(ChronoLocalDate<?> other) {
        return this.getLong(EPOCH_DAY) < other.getLong(EPOCH_DAY);
    }

    @Override
    public boolean equalDate(ChronoLocalDate<?> other) {
        return this.getLong(EPOCH_DAY) == other.getLong(EPOCH_DAY);
    }

//    //-------------------------------------------------------------------------
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj instanceof DefaultInterfaceChronoLocalDate) {
//            DefaultInterfaceChronoLocalDate<?> other = (DefaultInterfaceChronoLocalDate<?>) obj;
//            return get(EPOCH_DAY) == other.get(EPOCH_DAY) &&
//                    getChrono().equals(other.getChrono());
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        long epDay = get(EPOCH_DAY);
//        return getChrono().hashCode() ^ ((int) (epDay ^ (epDay >>> 32)));
//    }

    //-------------------------------------------------------------------------
    @Override
    public String toString() {
        // getLong() reduces chances of exceptions in toString()
        long yoe = getLong(YEAR_OF_ERA);
        long moy = getLong(MONTH_OF_YEAR);
        long dom = getLong(DAY_OF_MONTH);
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChrono().toString())
                .append(" ")
                .append(getEra())
                .append(yoe)
                .append(moy < 10 ? "-0" : "-").append(moy)
                .append(dom < 10 ? "-0" : "-").append(dom);
        return buf.toString();
    }

    @Override
    public String toString(CalendricalFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.print(this);
    }

}
