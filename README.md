# Body-doubling PSN plugin

### Plans
1. Uses same login as the rest of the ecosystem
2. Able to track actions as entered
    1. Daily actions will just be a timestamp and a text description of the action
    2. UI should offer current time as default for timestamp, but allow user to enter different time
    3. Activities are tied to a "day" logically, rather than the literal date. That is, a person may consider an action taken at 1am on Tuesday to be part of their Monday actions.
3. Able to plan daily goals
    1. Goal can be repeatable as "one time", "daily", "day of week", "day of month", and "day of year".
4. Able to connect daily activities to goals
    1. This should be many-to-many
5. Capacity for users to react to or comment on
    1. Reactions available on activities, goals, and a whole day
    2. By default offer "like" and "love" reactions
6. Configurable TTL

### db Data types
1. daily activities
   	1. activity time: time
   	2. activity date: date
	3. activity description: text
	4. id: id
	5. acting_user: id
	6. referencing goal: id[]
2. one-time activity goal
	1. goal description: text
	2. date: date
	3. id: id
	4. goal_user: id
3. daily activity goal
	1. goal description: text
	2. id: id
	3. goal_user: id
4. weekly activity goal
	1. goal description: text
	2. days_of_week: int[]
	3. id: id
	4. goal_user: id
5. monthly activity goal
	1. goal description: text
	2. days_of_month: int[]
	3. id: id
	4. goal_user: id
6. annual activity goal
	1. goal description: text
	2. month_and_day: tuple<int, int>[]
	3. id: id
	4. goal_user: id
8. day reaction
	1. reacting user: id
	2. reaction: id
	3. reaction time: timestamp
	4. date: date (corresponds to the date being reacted to)
	5. user_reacted_to: id
9. day comment
	1. commenter: id (user id)
	2. comment: text
	3. comment time: timestamp
	4. date: date (corresponds to the date being commented on)
	5. user_reacted_to: id
10. activity/goal reaction
	1. reacting user: id
	2. reaction: id
	3. reaction time: timestamp
	4. reacting_item_id: id
	5. reacting_item_type: int
11. activity/goal comment
	1. commenter: id (user id)
	2. comment: text
	3. reaction time: timestamp
	4. commenting_item_id: id
	5. commenting_item_type: int
	
### Model data types
	1. daily-event
	{
		"date": DATE,
		"goals": goal[],
		"activities": activity[]
	}
	2. goal
	{
		"id": UUID,
		"date": DATE,
		"repetitionType": STRING,
		"repeatsOn": INT[] | MAP<INT, INT>, //leave out for one-time, int[] for weekly and monthly, map for yearly
		"reactions": MAP<UUID, INT>,
		"comments": comment[]
	}
	3. reaction
	{
		"id": UUID,
		"reactorId": UUID,
		"reactorName": STRING,
		"reactionTime": TIMESTAMP
	}
	4. comment
	{
		"id": UUID,
		"commenterId": UUID,
		"commenterName": STRING,
		"commentText": STRING
	}
	5. activity
	{
		"id": UUID,
		"date": DATE,
		"time": TIME,
		"activityDescription": STRING,
		"reactions": MAP<UUID, int>,
		"comments": comment[],
		"referencingGoal": UUID
	}
	
### Endpoints
1. GET /daily-event/{date}
	* request body: N/A
	* path param - date: "yyyyMMdd" of the date
2.  GET /daily-event //Special case of the above that returns the current date
	* request body: N/A
	* response body: daily-event
	* response body: daily-event
3. POST /goal
	* request body: goal
	* response body: goal
4. PUT /goal
	* request body: goal
	* response body: goal
5. POST /activity
	* request body: activity
	* response body: activity
6. PUT /activity
	* request body: activity
	* response body: activity
7. GET /goal/{id}
	* request body: N/A
	* path param - id: ID of goal to return
	* response body: goal
8. GET /activity/{id}
	* request body: N/A
	* path param - id: ID of activity to return
	* response body: activity
