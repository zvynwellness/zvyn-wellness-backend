USE railway;

DELETE FROM product;
DELETE FROM product_category;
ALTER TABLE product AUTO_INCREMENT = 1;
ALTER TABLE product_category AUTO_INCREMENT = 1;

INSERT INTO product_category (category_name) VALUES
  ('Restore'),
  ('Rebalance'),
  ('Rise');

INSERT INTO product (name, description, image_url, active, category_id, date_created) VALUES
  ('Rest & Reset Session', 'A calming one-to-one session to understand stress patterns and create a realistic recovery rhythm.', 'assets/images/products/wellness/restore.svg', 1, 1, NOW()),
  ('Nourish Foundations', 'Personalized guidance for everyday nourishment, energy, and habits that feel sustainable.', 'assets/images/products/wellness/nourish.svg', 1, 1, NOW()),
  ('Rhythm Rebalance', 'Build a supportive weekly rhythm around sleep, movement, focus, and meaningful rest.', 'assets/images/products/wellness/rebalance.svg', 1, 2, NOW()),
  ('Mindful Energy Coaching', 'Notice what drains and restores you, then turn those insights into practical boundaries and rituals.', 'assets/images/products/wellness/mindful.svg', 1, 2, NOW()),
  ('Clarity Intensive', 'A focused session for a season of change, helping you identify priorities and a grounded next step.', 'assets/images/products/wellness/clarity.svg', 1, 3, NOW()),
  ('Whole-Self Journey', 'Ongoing personal support that connects wellbeing goals with the way you want to live and feel.', 'assets/images/products/wellness/journey.svg', 1, 3, NOW());
