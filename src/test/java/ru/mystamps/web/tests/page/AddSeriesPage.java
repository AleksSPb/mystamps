/*
 * Copyright (C) 2009-2012 Slava Semushin <slava.semushin@gmail.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package ru.mystamps.web.tests.page;

import java.util.List;

import org.openqa.selenium.WebDriver;

import ru.mystamps.web.Url;

import static ru.mystamps.web.tests.TranslationUtils.tr;
import static ru.mystamps.web.tests.page.element.Form.with;
import static ru.mystamps.web.tests.page.element.Form.inputField;
import static ru.mystamps.web.tests.page.element.Form.checkboxField;
import static ru.mystamps.web.tests.page.element.Form.selectField;
import static ru.mystamps.web.tests.page.element.Form.textareaField;
import static ru.mystamps.web.tests.page.element.Form.uploadFileField;
import static ru.mystamps.web.tests.page.element.Form.submitButton;

public class AddSeriesPage extends AbstractPageWithForm {
	
	public AddSeriesPage(final WebDriver driver) {
		super(driver, Url.ADD_SERIES_PAGE);
		
		hasForm(
			with(
				selectField("country").withLabel(tr("t_country")),
				selectField("year").withLabel(tr("t_issue_year")),
				inputField("quantity").withLabel(tr("t_quantity")),
				checkboxField("perforated").withLabel(tr("t_perforated")),
				inputField("michelNumbers").withLabel(tr("t_michel_no")),
				inputField("scottNumbers").withLabel(tr("t_scott_no")),
				inputField("yvertNumbers").withLabel(tr("t_yvert_no")),
				inputField("gibbonsNumbers").withLabel(tr("t_sg_no")),
				textareaField("comment").withLabel(tr("t_comment")),
				uploadFileField("image").withLabel(tr("t_image"))
			)
			.and()
			.with(submitButton(tr("t_add")))
		);
	}
	
	public List<String> getContryFieldValues() {
		return getSelectOptions("country");
	}
	
	public List<String> getYearFieldValues() {
		return getSelectOptions("year");
	}
	
}
