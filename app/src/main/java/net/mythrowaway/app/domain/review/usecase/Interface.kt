package net.mythrowaway.app.domain.review.usecase

import net.mythrowaway.app.domain.review.entity.Review

interface ReviewRepositoryInterface {
  fun find(): Review?
  fun save(review: Review)
}
