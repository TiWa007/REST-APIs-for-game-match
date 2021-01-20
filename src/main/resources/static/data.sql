INSERT INTO users(user_id, name, gender, nickname, geography)
VALUES
       (10001, 'Name10001', 'male', 'nickname10001', 'Europe' ),
       (10002, 'Name10002', 'male', 'nickname10002', 'Asia' ),
       (10003, 'Name10003', 'female', 'nickname10003', 'Europe' ),
       (10004, 'Name10004', 'male', 'nickname10004', 'Europe' ),
       (10005, 'Name10005', 'female', 'nickname10005', 'Asia' ),
       (10006, 'Name10006', 'female', 'nickname10006', 'Europe' ),
       (10007, 'Name10007', 'male', 'nickname10007', 'USA' ),
       (10008, 'Name10008', 'female', 'nickname10008', 'Asia' ),
       (10009, 'Name10009', 'male', 'nickname10009', 'Europe' ),
       (10010, 'Name10010', 'male', 'nickname10010', 'USA' );

INSERT INTO interests(interest_id, game, level, credit, user_id)
VALUES
       (10001, 'fortnite', 'noob', 10, '10001' ),
       (10002, 'call of duty', 'noob', 8, '10001' ),
       (10003, 'fortnite', 'noob', 5, '10002' ),
       (10004, 'dota', 'noob', 4, '10002' ),
       (10005, 'among us', 'pro', 3, '10002' ),
       (10006, 'fortnite', 'noob', 0, '10003' ),
       (10007, 'among us', 'invincible', 12, '10003' ),
       (10008, 'dota', 'pro', 6, '10004' ),
       (10009, 'dota', 'noob', 5, '10005' ),
       (10010, 'among us', 'invincible', 4, '10006' ),
       (10011, 'fortnite', 'noob', 3, '10007' ),
       (10012, 'valhalla', 'pro', 0, '10008' ),
       (10013, 'dota', 'invincible', 10, '10008' ),
       (10014, 'valhalla', 'pro', 9, '10009' ),
       (10015, 'valhalla', 'noob', 0, '10010' );

