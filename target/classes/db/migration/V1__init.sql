-- Enable uuid extension if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS daily_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    date DATE NOT NULL,
    sleep_hours INTEGER,
    diet_quality VARCHAR(20),
    notes VARCHAR(2000),
    user_id UUID REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS behavior_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_date_time TIMESTAMP NOT NULL,
    intensity VARCHAR(20),
    duration_minutes INTEGER,
    antecedent VARCHAR(2000),
    behavior VARCHAR(2000),
    consequence VARCHAR(2000),
    user_id UUID REFERENCES users(id)
);

