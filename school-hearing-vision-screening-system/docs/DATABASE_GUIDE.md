# Database guide

The schema is in `database/schema.sql`.

Relationship summary:

- One pupil can have many screening records.
- One pupil can have many referrals.
- One screening can optionally be linked to a referral.
- Each screening and referral records the authorized user who entered it.

The schema deliberately contains no real pupil records or default administrator credentials. Create the first administrator locally with `SetupAdmin`.
