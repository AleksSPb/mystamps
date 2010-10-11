package ru.mystamps.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lombok.Cleanup;

import org.springframework.stereotype.Repository;

@Repository
public class SuspiciousActivities {
	private final DataSource ds;
	
	private static final String logEventQuery =
			"INSERT INTO `suspicious_activities` " +
			"SELECT id, NOW(), ?, ?, ?, ?, ? " +
			"FROM `suspicious_activities_types` " +
			"WHERE name = ?";
	
	/**
	 * @throws NamingException
	 **/
	public SuspiciousActivities()
		throws NamingException {
		
		final Context env =
			(Context)new InitialContext().lookup("java:comp/env");
		ds = (DataSource)env.lookup("jdbc/mystamps");
	}
	
	/**
	 * Add record about suspicious activity.
	 *
	 * Parameters ip, refererPage, or userAgent may equals to null, in this case
	 * empty values will be saved to database.
	 *
	 * @todo check length of arguments and warn() if its greater than field size
	 *
	 * @param String type
	 * @param String page
	 * @param Long   uid user id or null for not authenticated users
	 * @param String ip
	 * @param String refererPage
	 * @param String userAgent
	 * @throws SQLException
	 **/
	public void logEvent(final String type,
			final String page,
			final Long uid,
			final String ip,
			final String refererPage,
			final String userAgent)
		throws SQLException {
		
		@Cleanup
		final Connection conn = ds.getConnection();
		
		@Cleanup
		final PreparedStatement stat =
			conn.prepareStatement(logEventQuery);
		
		stat.setString(1, page);
		
		if (uid != null) {
			stat.setLong(2, uid);
		} else {
			stat.setNull(2, Types.INTEGER);
		}
		
		stat.setString(3, (ip == null ? "" : ip));
		stat.setString(4, (refererPage == null ? "" : refererPage));
		stat.setString(5, (userAgent == null ? "" : userAgent));
		stat.setString(6, type);
		
		// TODO: check return value. When we use type which not exists
		// in suspicious_activities_types table, then logs may be
		// silently not added.
		stat.executeUpdate();
	}
	
}
