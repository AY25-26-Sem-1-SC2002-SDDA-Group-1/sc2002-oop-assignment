# Changelog

Hey there! Here's what's been cooking in the Internship Placement System. We've been busy adding features, fixing bugs, and making things smoother for everyone.

## Version 2.0.0 - November 16, 2025

### Big New Stuff

**Flexible Withdrawals**: Students can now back out of applications at any point - pending, successful, or even confirmed. The system handles this smartly, only messing with queues when it's a confirmed placement.

**Batch Operations**: Tired of doing things one by one? Now you can apply to multiple internships, process applications, or toggle visibility for several internships all at once using space-separated IDs.

**Waitlist System**: When an internship fills up, students get queued automatically. First in, first out - and when a spot opens up, the next person gets confirmed right away.

**GPA Filtering**: Internships can now have minimum GPA requirements, and students are filtered based on their GPA eligibility.

**Statistics Dashboard**: Both students and company reps get detailed statistics on their activities - application success rates, internship fill rates, and more.

**Application Persistence**: Applications are now saved to CSV files, so they persist between sessions.

**User Registration System**: Added self-registration for students and career center staff, plus approval-based registration for company representatives. No more reliance on pre-loaded CSV files only.

### Better User Experience

**Pre-Show Lists**: Before you make decisions, you see what's available. Whether it's accepting offers or requesting withdrawals, you get the full picture first.

**Clean UI**: Ditched the emojis for professional text markers like [SUCCESS] and [FAILED]. Everything looks consistent now.

**Password Smarts**: Can't set your new password to be the same as your old one. Basic security, but hey, it works.

### Under the Hood Improvements

**Smart Tracking**: We remember if you withdrew manually so you can't just reapply to the same internship.

**Auto Status Updates**: Internships that were filled become available again when spots open up.

**Better Code Organization**: Centralized UI helpers, persistent filters, and solid validation everywhere.

**Data Persistence**: Everything saves properly now, including application states and queue info.

### Bug Squashes

- Fixed visibility issues for students
- Application limits now work correctly
- Status changes propagate properly
- Queue edge cases handled

### Docs and Stuff

- README updated with all the new features
- Better workflow docs
- Updated diagrams
- Test verification docs

## Version 1.0.0 - Initial Release

The basics: login system for students, company reps, and staff. Create internships, apply, approve, track everything. CSV data storage, filtering by major/year/dates. Simple but solid foundation.