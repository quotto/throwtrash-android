package net.mythrowaway.app.module.review.usecase

import net.mythrowaway.app.module.review.entity.Review

interface ReviewRepositoryInterface {
  fun find(): Review?
  fun save(review: Review)
}
