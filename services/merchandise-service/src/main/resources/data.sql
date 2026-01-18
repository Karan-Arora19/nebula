-- Dev seed data for merchandise products (schema-qualified)

-- Ensure columns exist (safe to run multiple times)
ALTER TABLE merchandise_service.products ADD COLUMN IF NOT EXISTS category varchar(255);
ALTER TABLE merchandise_service.products ADD COLUMN IF NOT EXISTS badge varchar(100);
ALTER TABLE merchandise_service.products ADD COLUMN IF NOT EXISTS rating numeric(3,1);
ALTER TABLE merchandise_service.products ADD COLUMN IF NOT EXISTS reviews integer;

DELETE FROM merchandise_service.products;

INSERT INTO merchandise_service.products (name, description, price, stock, image_url, category, badge, rating, reviews) VALUES
  ('Nebula Racing Cap', 'Premium cap', 35.00, 100, 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=400', 'Apparel', 'Bestseller', 4.8, 124),
  ('Team Jacket', 'Warm team jacket', 129.00, 80, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?w=400', 'Apparel', 'Premium', 4.9, 89),
  ('Car Model 1:18 Apex', 'Detailed scale model', 89.00, 60, 'https://images.unsplash.com/photo-1581235720704-06d3acfcb36f?w=400', 'Models', 'Limited', 5.0, 203),
  ('Premium Keychain', 'Premium metal keychain', 25.00, 200, 'https://unsplash.com/photos/hbCqHtYeUZ8/download?force=true&w=400', 'Accessories', 'Premium', 4.6, 312),
  ('Carbon Fiber Wallet', 'Slim carbon fiber wallet', 78.00, 120, 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=400', 'Accessories', 'New', 4.7, 156),
  ('Racing Gloves', 'High grip racing gloves', 65.00, 90, 'https://unsplash.com/photos/5fnmt6S7y4o/download?force=true&w=400', 'Apparel', NULL, 4.8, 98),
  ('Nebula Coffee Mug Set', 'Ceramic mug set', 32.00, 150, 'https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400', 'Lifestyle', NULL, 4.5, 267),
  ('Performance T-Shirt', 'Breathable performance tee', 45.00, 140, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', 'Apparel', 'Bestseller', 4.7, 189),
  ('Leather Driving Shoes', 'Comfort driving shoes', 145.00, 70, 'https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400', 'Apparel', 'Premium', 4.9, 76),
  ('Backpack - Velocity Series', 'Durable commuter backpack', 95.00, 110, 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400', 'Accessories', 'New', 4.8, 142),
  ('Watch - Limited Edition', 'Chronograph watch', 299.00, 40, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400', 'Accessories', 'Limited', 5.0, 54),
  ('Sunglasses - Sport', 'Polarized sport sunglasses', 89.00, 130, 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=400', 'Accessories', NULL, 4.6, 223);
