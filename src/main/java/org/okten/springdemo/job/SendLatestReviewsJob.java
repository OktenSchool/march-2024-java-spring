package org.okten.springdemo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.okten.springdemo.dto.ReviewDTO;
import org.okten.springdemo.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendLatestReviewsJob {

	private final MailSender mailSender;

	private final ProductService productService;

	@Value("${spring.mail.username}")
	private String mailUsername;

	@Scheduled(fixedDelay = 10, timeUnit = TimeUnit.SECONDS)
	public void sendLatestReviews() {
		log.info("Send latest reviews job is running");

		final List<ReviewDTO> latestReviews = this.productService
				.getLatestReviews(LocalDateTime.now().minusSeconds(10));

		if (latestReviews.isEmpty()) {
			return;
		}

		final SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(this.mailUsername);
		message.setTo(this.mailUsername);

		message.setSubject("Latest reviews");
		final String text = latestReviews.stream().map(ReviewDTO::toString)
				.collect(Collectors.joining(", \n", "Here are the latest reviews: \n", ""));
		message.setText(text);

		log.info("Sending mail about latest reviews...");
//		this.mailSender.send(message);
	}

	// fixedDelay = 1 hour
	// 1 run - 13:00 - 13:05
	// 2 run - 14:05 - 14:10
	// 3 run - 15:10 - 15:15

	// fixedRate = 2 minutes
	// 1 run - 13:00 - 13:05
	// 2 run - 14:00 - 14:05
	// 3 run - 15:00 - 15:05
}
