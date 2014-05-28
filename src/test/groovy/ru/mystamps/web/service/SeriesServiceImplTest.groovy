/**
 * Copyright (C) 2009-2014 Slava Semushin <slava.semushin@gmail.com>
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
package ru.mystamps.web.service

import org.springframework.web.multipart.MultipartFile

import spock.lang.Specification
import spock.lang.Unroll

import ru.mystamps.web.dao.SeriesDao
import ru.mystamps.web.entity.Category
import ru.mystamps.web.entity.Country
import ru.mystamps.web.entity.Currency
import ru.mystamps.web.entity.GibbonsCatalog
import ru.mystamps.web.entity.MichelCatalog
import ru.mystamps.web.entity.ScottCatalog
import ru.mystamps.web.entity.Series
import ru.mystamps.web.entity.User
import ru.mystamps.web.entity.YvertCatalog
import ru.mystamps.web.model.AddSeriesForm
import ru.mystamps.web.tests.DateUtils

class SeriesServiceImplTest extends Specification {
	private static final Double ANY_PRICE = 17d
	
	private ImageService imageService = Mock()
	private SeriesDao seriesDao = Mock()
	private MultipartFile multipartFile = Mock()
	
	private SeriesService service
	private AddSeriesForm form
	private User user
	
	def setup() {
		form = new AddSeriesForm()
		form.setQuantity(2)
		form.setPerforated(false)
		form.setCategory(TestObjects.createCategory())
		
		user = TestObjects.createUser()
		
		imageService.save(_) >> "/fake/path/to/image"
		
		service = new SeriesServiceImpl(seriesDao, imageService)
	}
	
	//
	// Tests for add()
	//
	
	def "add() should throw exception argument is null"() {
		when:
			service.add(null, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if quantity is null"() {
		given:
			form.setQuantity(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if perforated is null"() {
		given:
			form.setPerforated(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception if category is null"() {
		given:
			form.setCategory(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should throw exception when user is null"() {
		when:
			service.add(form, null, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass entity to series dao"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save(_ as Series)
	}
	
	def "add() should load and pass country to series dao if country present"() {
		given:
			Country expectedCountry = TestObjects.createCountry()
			form.setCountry(expectedCountry)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.country == expectedCountry
				return true
			})
	}
	
	def "add() should pass year to series dao if year present"() {
		given:
			int expectedYear = 2000
			form.setYear(expectedYear)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.releaseYear == expectedYear
				return true
			})
	}
	
	def "add() should pass category to series dao"() {
		given:
			Category expectedCategory = TestObjects.createCategory()
			form.setCategory(expectedCategory)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.category == expectedCategory
				return true
			})
	}
	
	def "add() should pass quantity to series dao"() {
		given:
			Integer expectedQuantity = 3
			form.setQuantity(expectedQuantity)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.quantity == expectedQuantity
				return true
			})
	}
	
	def "add() should pass perforated to series dao"() {
		given:
			Boolean expectedResult = true
			form.setPerforated(expectedResult)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.perforated == expectedResult
				return true
			})
	}

	def "add() should pass null to series dao if michel numbers is null"() {
		given:
			assert form.getMichelNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == null
				return true
			})
	}
	
	def "add() should pass michel numbers to series dao"() {
		given:
			Set<MichelCatalog> expectedNumbers = [
				new MichelCatalog("1"),
				new MichelCatalog("2")
			] as Set
			form.setMichelNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michel == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if michel price specified without currency"() {
		given:
			form.setMichelPrice(ANY_PRICE)
		and:
			form.setMichelCurrency(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass michel price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setMichelPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setMichelCurrency(expectedCurrency)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice?.value == expectedPrice
				assert series?.michelPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if michel price is null"() {
		given:
			form.setMichelPrice(null)
		and:
			form.setMichelCurrency(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.michelPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if scott numbers is null"() {
		given:
			assert form.getScottNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == null
				return true
			})
	}
	
	def "add() should pass scott numbers to series dao"() {
		given:
			Set<ScottCatalog> expectedNumbers = [
				new ScottCatalog("1"),
				new ScottCatalog("2")
			] as Set
			form.setScottNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scott == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if scott price specified without currency"() {
		given:
			form.setScottPrice(ANY_PRICE)
		and:
			form.setScottCurrency(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass scott price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setScottPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setScottCurrency(expectedCurrency)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice?.value == expectedPrice
				assert series?.scottPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if scott price is null"() {
		given:
			form.setScottPrice(null)
		and:
			form.setScottCurrency(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.scottPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if yvert numbers is null"() {
		given:
			assert form.getYvertNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == null
				return true
			})
	}
	
	def "add() should pass yvert numbers to series dao"() {
		given:
			Set<YvertCatalog> expectedNumbers = [
				new YvertCatalog("1"),
				new YvertCatalog("2")
			] as Set
			form.setYvertNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvert == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if yvert price specified without currency"() {
		given:
			form.setYvertPrice(ANY_PRICE)
		and:
			form.setYvertCurrency(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass yvert price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setYvertPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setYvertCurrency(expectedCurrency)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice?.value == expectedPrice
				assert series?.yvertPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if yvert price is null"() {
		given:
			form.setYvertPrice(null)
		and:
			form.setYvertCurrency(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.yvertPrice == null
				return true
			})
	}
	
	def "add() should pass null to series dao if gibbons numbers is null"() {
		given:
			assert form.getGibbonsNumbers() == null
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == null
				return true
			})
	}
	
	def "add() should pass gibbons numbers to series dao"() {
		given:
			Set<GibbonsCatalog> expectedNumbers = [
				new GibbonsCatalog("1"),
				new GibbonsCatalog("2")
			] as Set
			form.setGibbonsNumbers(expectedNumbers.join(','))
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbons == expectedNumbers
				return true
			})
	}
	
	def "add() should throw exception if gibbons price specified without currency"() {
		given:
			form.setGibbonsPrice(ANY_PRICE)
		and:
			form.setGibbonsCurrency(null)
		when:
			service.add(form, user, false)
		then:
			thrown IllegalArgumentException
	}
	
	def "add() should pass gibbons price to series dao"() {
		given:
			Double expectedPrice = ANY_PRICE
			form.setGibbonsPrice(expectedPrice)
		and:
			Currency expectedCurrency = Currency.RUR
			form.setGibbonsCurrency(expectedCurrency)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice?.value == expectedPrice
				assert series?.gibbonsPrice?.currency == expectedCurrency
				return true
			})
	}
	
	def "add() should pass null to series dao if gibbons price is null"() {
		given:
			form.setGibbonsPrice(null)
		and:
			form.setGibbonsCurrency(null)
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.gibbonsPrice == null
				return true
			})
	}
	
	def "add() should pass image to image service"() {
		given:
			form.setImage(multipartFile)
		when:
			service.add(form, user, false)
		then:
			1 * imageService.save({ MultipartFile passedFile ->
				assert passedFile == multipartFile
				return true
			}) >> "/any/path"
	}
	
	def "add() should throw exception if image url is null"() {
		when:
			service.add(form, user, false)
		then:
			// override setup() settings
			imageService.save(_) >> null
		and:
			thrown IllegalStateException
	}
	
	def "add() should throw exception if image url too long"() {
		when:
			service.add(form, user, false)
		then:
			// override setup() settings
			imageService.save(_) >> "x" * (Series.IMAGE_URL_LENGTH + 1)
		and:
			thrown IllegalStateException
	}
	
	def "add() should pass image url to series dao"() {
		given:
			String expectedUrl = "http://example.org/example.jpg"
		when:
			service.add(form, user, false)
		then:
			imageService.save(_) >> expectedUrl
		and:
			1 * seriesDao.save({ Series series ->
				assert series?.imageUrl == expectedUrl
				return true
			})
	}
	
	def "add() should throw exception if comment is empty"() {
		given:
			form.setComment("  ")
		when:
			service.add(form, user, true)
		then:
			thrown IllegalArgumentException
	}
	
	@Unroll
	def "add() should pass '#expectedComment' as comment to series dao if user can add comment is #canAddComment"(boolean canAddComment, String comment, String expectedComment) {
		given:
			form.setComment(comment)
		when:
			service.add(form, user, canAddComment)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.comment == expectedComment
				return true
			})
		where:
			canAddComment | comment     || expectedComment
			false         | null        || null
			false         | "test"      || null
			true          | null        || null
			true          | "Some text" || "Some text"
	}
	
	def "add() should assign created at to current date"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.createdAt, new Date())
				return true
			})
	}
	
	def "add() should assign updated at to current date"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert DateUtils.roughlyEqual(series?.metaInfo?.updatedAt, new Date())
				return true
			})
	}
	
	def "add() should assign created by to user"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.createdBy == user
				return true
			})
	}
	
	def "add() should assign updated by to user"() {
		when:
			service.add(form, user, false)
		then:
			1 * seriesDao.save({ Series series ->
				assert series?.metaInfo?.updatedBy == user
				return true
			})
	}
	
	//
	// Tests for countAll()
	//
	
	def "countAll() should call dao and returns result"() {
		given:
			long expectedResult = 20
		when:
			long result = service.countAll()
		then:
			1 * seriesDao.count() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countAllStamps()
	//
	
	def "countAllStamps() should call dao and returns result"() {
		given:
			long expectedResult = 30
		when:
			long result = service.countAllStamps()
		then:
			1 * seriesDao.countAllStamps() >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByMichelNumber()
	//
	
	@Unroll
	def "countByMichelNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode, Object _) {
		when:
			service.countByMichelNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			""          | _
			" "         | _
	}
	
	def "countByMichelNumber() should pass argument to dao and return result"() {
		given:
			int expectedResult = 1
		when:
			int result = service.countByMichelNumber("7")
		then:
			1 * seriesDao.countByMichelNumberCode({ String code ->
				assert code == "7"
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByScottNumber()
	//
	
	@Unroll
	def "countByScottNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode, Object _) {
		when:
			service.countByScottNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			""          | _
			" "         | _
	}
	
	def "countByScottNumber() should pass argument to dao and return result"() {
		given:
			int expectedResult = 2
		when:
			int result = service.countByScottNumber("8")
		then:
			1 * seriesDao.countByScottNumberCode({ String code ->
				assert code == "8"
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByYvertNumber()
	//
	
	@Unroll
	def "countByYvertNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode, Object _) {
		when:
			service.countByYvertNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			""          | _
			" "         | _
	}
	
	def "countByYvertNumber() should pass argument to dao and return result"() {
		given:
			int expectedResult = 3
		when:
			int result = service.countByYvertNumber("9")
		then:
			1 * seriesDao.countByYvertNumberCode({ String code ->
				assert code == "9"
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for countByGibbonsNumber()
	//
	
	@Unroll
	def "countByGibbonsNumber() should throw exception for invalid argument '#catalogCode'"(String catalogCode, Object _) {
		when:
			service.countByGibbonsNumber(catalogCode)
		then:
			thrown IllegalArgumentException
		where:
			catalogCode | _
			null        | _
			""          | _
			" "         | _
	}
	
	def "countByGibbonsNumber() should pass argument to dao and return result"() {
		given:
			int expectedResult = 4
		when:
			int result = service.countByGibbonsNumber("10")
		then:
			1 * seriesDao.countByGibbonsNumberCode({ String code ->
				assert code == "10"
				return true
			}) >> expectedResult
		and:
			result == expectedResult
	}
	
	//
	// Tests for findBy(Category)
	//
	
	def "findBy(Category) should throw exception if category is null"() {
		when:
			service.findBy(null as Category)
		then:
			thrown IllegalArgumentException
	}
	
	def "findBy(Category) should call dao and return result"() {
		given:
			Category expectedCategory = TestObjects.createCategory()
		and:
			Iterable<Category> expectedResult = [ expectedCategory ]
		and:
			seriesDao.findByAsSeriesInfo(_ as Category) >> expectedResult
		when:
			Iterable<Category> result = service.findBy(expectedCategory)
		then:
			result == expectedResult
	}
	
	//
	// Tests for findBy(Country)
	//
	
	def "findBy(Country) should throw exception if country is null"() {
		when:
			service.findBy(null as Country)
		then:
			thrown IllegalArgumentException
	}
	
	def "findBy(Country) should call dao and return result"() {
		given:
			Country expectedCountry = TestObjects.createCountry()
		and:
			Iterable<Country> expectedResult = [ expectedCountry ]
		and:
			seriesDao.findByAsSeriesInfo(_ as Country) >> expectedResult
		when:
			Iterable<Country> result = service.findBy(expectedCountry)
		then:
			result == expectedResult
	}
	
}
