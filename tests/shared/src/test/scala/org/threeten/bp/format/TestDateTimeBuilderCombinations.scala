/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
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
package org.threeten.bp.format

import org.scalatest.funsuite.AnyFunSuite
import org.threeten.bp.AssertionsHelper
import org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH
import org.threeten.bp.temporal.ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR
import org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_MONTH
import org.threeten.bp.temporal.ChronoField.ALIGNED_WEEK_OF_YEAR
import org.threeten.bp.temporal.ChronoField.DAY_OF_MONTH
import org.threeten.bp.temporal.ChronoField.DAY_OF_WEEK
import org.threeten.bp.temporal.ChronoField.DAY_OF_YEAR
import org.threeten.bp.temporal.ChronoField.EPOCH_DAY
import org.threeten.bp.temporal.ChronoField.MONTH_OF_YEAR
import org.threeten.bp.temporal.ChronoField.PROLEPTIC_MONTH
import org.threeten.bp.temporal.ChronoField.YEAR
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.temporal.TemporalAccessor
import org.threeten.bp.temporal.TemporalField
import org.threeten.bp.temporal.TemporalQuery

/** Test. */
object TestDateTimeBuilderCombinations {
  private val PARIS: ZoneId = ZoneId.of("Europe/Paris")

  private val localDateFrom = new TemporalQuery[LocalDate]() {
    def queryFrom(temporal: TemporalAccessor): LocalDate = LocalDate.from(temporal)
  }
}

class TestDateTimeBuilderCombinations extends AnyFunSuite with AssertionsHelper {
  import TestDateTimeBuilderCombinations.localDateFrom

  val data_combine: List[
    (
      TemporalField,
      Number,
      TemporalField,
      Number,
      TemporalField,
      Number,
      TemporalField,
      Number,
      TemporalQuery[LocalDate],
      AnyRef
    )
  ] = {
    List(
      (YEAR,
       2012,
       MONTH_OF_YEAR,
       6,
       DAY_OF_MONTH,
       3,
       null,
       null,
       localDateFrom,
       LocalDate.of(2012, 6, 3)),
      (PROLEPTIC_MONTH,
       2012 * 12 + 6 - 1,
       DAY_OF_MONTH,
       3,
       null,
       null,
       null,
       null,
       localDateFrom,
       LocalDate.of(2012, 6, 3)),
      (YEAR,
       2012,
       ALIGNED_WEEK_OF_YEAR,
       6,
       DAY_OF_WEEK,
       3,
       null,
       null,
       localDateFrom,
       LocalDate.of(2012, 2, 8)),
      (YEAR,
       2012,
       DAY_OF_YEAR,
       155,
       null,
       null,
       null,
       null,
       localDateFrom,
       LocalDate.of(2012, 6, 3)),
      (EPOCH_DAY, 12, null, null, null, null, null, null, localDateFrom, LocalDate.of(1970, 1, 13))
    )
  }

  test("test_derive") {
    data_combine.foreach {
      case (field1, value1, field2, value2, field3, value3, field4, value4, query, expectedVal) =>
        val builder: DateTimeBuilder = new DateTimeBuilder(field1, value1.longValue)
        builder.chrono = IsoChronology.INSTANCE
        if (field2 != null) {
          builder.addFieldValue(field2, value2.longValue)
        }
        if (field3 != null) {
          builder.addFieldValue(field3, value3.longValue)
        }
        if (field4 != null) {
          builder.addFieldValue(field4, value4.longValue)
        }
        builder.resolve(ResolverStyle.SMART, null)
        assertEquals(builder.build(query), expectedVal)
      case _ =>
        fail()
    }
  }

  val data_normalized: List[
    (TemporalField, Number, TemporalField, Number, TemporalField, Number, TemporalField, Number)
  ] =
    List(
      (YEAR, 2127, null, null, null, null, YEAR, 2127),
      (MONTH_OF_YEAR, 12, null, null, null, null, MONTH_OF_YEAR, 12),
      (DAY_OF_YEAR, 127, null, null, null, null, DAY_OF_YEAR, 127),
      (DAY_OF_MONTH, 23, null, null, null, null, DAY_OF_MONTH, 23),
      (DAY_OF_WEEK, 127, null, null, null, null, DAY_OF_WEEK, 127L),
      (ALIGNED_WEEK_OF_YEAR, 23, null, null, null, null, ALIGNED_WEEK_OF_YEAR, 23),
      (ALIGNED_DAY_OF_WEEK_IN_YEAR, 4, null, null, null, null, ALIGNED_DAY_OF_WEEK_IN_YEAR, 4L),
      (ALIGNED_WEEK_OF_MONTH, 4, null, null, null, null, ALIGNED_WEEK_OF_MONTH, 4),
      (ALIGNED_DAY_OF_WEEK_IN_MONTH, 3, null, null, null, null, ALIGNED_DAY_OF_WEEK_IN_MONTH, 3),
      (PROLEPTIC_MONTH, 15, null, null, null, null, PROLEPTIC_MONTH, null),
      (PROLEPTIC_MONTH, 1971 * 12 + 4 - 1, null, null, null, null, YEAR, 1971),
      (PROLEPTIC_MONTH, 1971 * 12 + 4 - 1, null, null, null, null, MONTH_OF_YEAR, 4)
    )

  test("test_normalized") {
    data_normalized.foreach {
      case (field1, value1, field2, value2, field3, value3, query, expectedVal) =>
        val builder: DateTimeBuilder = new DateTimeBuilder(field1, value1.longValue)
        builder.chrono = IsoChronology.INSTANCE
        if (field2 != null) {
          builder.addFieldValue(field2, value2.longValue)
        }
        if (field3 != null) {
          builder.addFieldValue(field3, value3.longValue)
        }
        builder.resolve(ResolverStyle.SMART, null)
        if (expectedVal != null) {
          assertEquals(builder.getLong(query), expectedVal.longValue)
        } else {
          assertEquals(builder.isSupported(query), false)
        }
      case _ =>
        fail()
    }
  }

  test("test_parse_ZDT_withZone") {
    val fmt: DateTimeFormatter = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone(TestDateTimeBuilderCombinations.PARIS)
    val acc: TemporalAccessor = fmt.parse("2014-06-30 01:02:03")
    assertEquals(ZonedDateTime.from(acc),
                 ZonedDateTime.of(2014, 6, 30, 1, 2, 3, 0, TestDateTimeBuilderCombinations.PARIS))
  }

  test("test_parse_Instant_withZone") {
    val fmt: DateTimeFormatter = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss")
      .withZone(TestDateTimeBuilderCombinations.PARIS)
    val acc: TemporalAccessor = fmt.parse("2014-06-30 01:02:03")
    assertEquals(
      Instant.from(acc),
      ZonedDateTime.of(2014, 6, 30, 1, 2, 3, 0, TestDateTimeBuilderCombinations.PARIS).toInstant
    )
  }
}
