package com.uspray.uspray.domain.category.service;

import com.uspray.uspray.domain.category.dto.CategoryRequestDto;
import com.uspray.uspray.domain.category.dto.CategoryResponseDto;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.repository.CategoryRepository;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final MemberRepository memberRepository;
	private final CategoryRepository categoryRepository;

	private static int getNewOrder(int index, List<Category> categories, Category category) {
		Category targetPosition = categories.get(index - 1);

		if (targetPosition.equals(category)) { // 원하는 위치가 지금 내 위치와 같으면
			return targetPosition.getOrder();
		}

		if (index == 1) { // 원하는 위치가 첫번째면
			return getFirstPositionOrder(categories);
		}

		if (index == categories.size()) { // 원하는 위치가 마지막이면
			return getLastPositionOrder(targetPosition);
		}
		return getMiddlePositionOrder(targetPosition, category, categories, index);
	}

	private static void validateIndex(int index, int size) {
		if (index < 1 || index > size) {
			throw new NotFoundException(ErrorStatus.INDEX_OUT_OF_BOUND_EXCEPTION);
		}
	}

	private static int getFirstPositionOrder(List<Category> categories) {
		return categories.get(0).getOrder() / 2;
	}

	private static int getLastPositionOrder(Category prevCategory) {
		return prevCategory.getOrder() + 1024;
	}

	private static int getMiddlePositionOrder(Category targetCategory, Category currCategory,
		List<Category> categories, int index) {
		// 원하는 위치가 내 위치보다 앞이면 원하는 자리와 그 앞자리 중간으로
		// 원하는 위치가 내 위치보다 뒤면 원하는 자리와 그 뒷자리 중간으로
		int prevIndex = targetCategory.getOrder() < currCategory.getOrder() ? index - 2 : index;
		return (targetCategory.getOrder() + categories.get(prevIndex).getOrder()) / 2;
	}

	public List<Category> getCategoryListByMemberAndCategoryType(Member member,
		CategoryType categoryType) {
		return categoryRepository.findAllByMemberAndCategoryTypeOrderByOrderAsc(member,
			categoryType);
	}


	public Category getCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CustomException(ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION));
	}

	public Category getCategoryByIdAndMemberAndType(Long categoryId, Member member,
		CategoryType categoryType) {
		return categoryRepository.findByIdAndMemberAndCategoryType(categoryId, member, categoryType)
			.orElseThrow(() -> new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH));
	}

	public List<PrayListResponseDto> findAllWithOrderAndType(String username, String prayType,
		List<Long> prayIds) {
		return categoryRepository.findAllWithOrderAndType(username, prayType, prayIds);
	}

	public CategoryResponseDto createCategory(String username,
		CategoryRequestDto categoryRequestDto) {
		Member member = memberRepository.getMemberByUserId(username);
		int maxCategoryOrder = categoryRepository.checkDuplicateAndReturnMaxOrder(
			categoryRequestDto.getName(), member,
			CategoryType.valueOf(categoryRequestDto.getType().toUpperCase()));
		Category category = categoryRequestDto.toEntity(member, maxCategoryOrder + 1024);
		categoryRepository.save(category);
		return CategoryResponseDto.of(category);
	}

	public CategoryResponseDto deleteCategory(Category category) {
		categoryRepository.delete(category);
		return CategoryResponseDto.of(category);
	}

	@Transactional
	public CategoryResponseDto updateCategory(String username, Long categoryId,
		CategoryRequestDto categoryRequestDto) {
		Category category = categoryRepository.findByIdAndMemberAndCategoryType(categoryId,
				memberRepository.getMemberByUserId(username), CategoryType.PERSONAL)
			.orElseThrow(() -> new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH));
		if (categoryRequestDto.getName() != null && !categoryRequestDto.getName()
			.equals(category.getName())) {
			categoryRepository.checkDuplicate(categoryRequestDto.getName(), category.getMember(),
				CategoryType.valueOf(categoryRequestDto.getType().toUpperCase()));
		}
		category.update(categoryRequestDto);
		return CategoryResponseDto.of(category);
	}

	public CategoryResponseDto getCategory(String username, Long categoryId) {
		Category category = categoryRepository.findByIdAndMemberAndCategoryType(categoryId,
				memberRepository.getMemberByUserId(username), CategoryType.PERSONAL)
			.orElseThrow(() -> new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH));
		return CategoryResponseDto.of(category);
	}

	@Transactional
	public CategoryResponseDto updateCategoryOrder(String username, Long categoryId, int index) {
		Member member = memberRepository.getMemberByUserId(username);
		Category category = categoryRepository.findByIdAndMemberAndCategoryType(categoryId, member,
				CategoryType.PERSONAL)
			.orElseThrow(() -> new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH));
		List<Category> categories = categoryRepository.findAllByMemberAndCategoryTypeOrderByOrderAsc(
			member, category.getCategoryType());

		validateIndex(index, categories.size());
		int newOrder = getNewOrder(index, categories, category);

		category.updateOrder(newOrder);

		categoryRepository.save(category);

		return CategoryResponseDto.of(category);
	}

	public List<CategoryResponseDto> getCategoryList(String username, String categoryType) {
		Member member = memberRepository.getMemberByUserId(username);
		CategoryType convertCategoryType = CategoryType.valueOf(categoryType.toUpperCase());
		List<Category> categories = categoryRepository.findAllByMemberAndCategoryTypeOrderByOrderAsc(
			member, convertCategoryType);
		return categories.stream()
			.map(CategoryResponseDto::of)
			.collect(Collectors.toList());
	}
}
