#!/bin/sh

find . -type f -name 'Price.java' |
xargs egrep -l '@(Entity|Embeddable)' |
while read FILE; do
	TABLE=$(fgrep @Table "$FILE" | sed 's|.*name = "\([^"]\+\)".*|\1|')
	PACKAGE=$(grep ^package "$FILE" | sed 's|^package ||;s|;$||')
	SHORT_CLASS_NAME=$(grep '^public class' "$FILE" | sed 's|^public class ||;s|^\([^ ]\+\).*|\1|')
	FULL_CLASS_NAME="$PACKAGE.$SHORT_CLASS_NAME"
	EMBEDDABLE=$(fgrep '@Embeddable' "$FILE")
	
	if [ -z "$EMBEDDABLE" ]; then
		echo "<entity name=\"$SHORT_CLASS_NAME\" class=\"$FULL_CLASS_NAME\" access=\"FIELD\">"
	else
		echo "<embeddable class=\"$FULL_CLASS_NAME\" access=\"FIELD\">"
	fi
	
	echo "\t<attributes>"
	
	fgrep private "$FILE" | sed 's|^[\t ]\+private ||;s|;.*$||' |
		while read FIELD_TYPE FIELD_NAME; do
			ANNOTATIONS=$(tac "$FILE" | sed -n "/private $FIELD_TYPE $FIELD_NAME/,/^[\t ]*$/p" | sed '$d' | tac)
			
			# TODO: use simple <id name="id" /> ?
			IS_ID=$(echo "$ANNOTATIONS" | fgrep '@Id')
			if [ -n "$IS_ID" ]; then
				echo "\t\t<id name=\"$FIELD_NAME\">"
				echo "\t\t\t<column name=\"$FIELD_NAME\" />"
				if [ -n "$(echo "$ANNOTATIONS | fgrep '@GeneratedValue'")" ]; then
					echo "\t\t\t<generated-value />"
				fi
				echo "\t\t</id>"
				continue
			fi
			
			COLUMN=$(echo "$ANNOTATIONS" | fgrep '@Column')
			if [ -n "$COLUMN" ]; then
				echo "\t\t<basic name=\"$FIELD_NAME\">"
				COLUMN_NAME=$(echo "$COLUMN" | sed -n 's|.*name = "\([^"]\+\)".*|\1|p')
				if [ -n "$COLUMN_NAME" ]; then
					NAME=$COLUMN_NAME
				else
					NAME=$FIELD_NAME
				fi
				echo -n "\t\t\t<column name=\"$NAME\""
				LENGTH=$(echo "$COLUMN" | sed -n 's|.*length = \([^,)]\+\).*|\1|p')
				if [ -n "$LENGTH" ]; then
					if [ -z "$(echo "$LENGTH" | grep '^[0-9]\+$')" ]; then
						if [ -n "$(echo "$LENGTH" | fgrep '.')" ]; then
							CLS_NAME=$(echo "$LENGTH" | cut -d. -f1)
							FLD_NAME=$(echo "$LENGTH" | cut -d. -f2)
							REAL_LENGTH=$(fgrep "int $FLD_NAME" "$CLS_NAME.java" | sed 's|.*= \([^;]\+\);.*|\1|')
						else
							REAL_LENGTH=$(fgrep "int $LENGTH" "$FILE" | sed 's|.*= \([^;]\+\);.*|\1|')
						fi
					else
						REAL_LENGTH=$LENGTH
					fi
					echo -n " length=\"$REAL_LENGTH\""
				fi
				if [ -n "$(echo "$COLUMN" | fgrep 'nullable = false')" ]; then
					echo -n " nullable=\"false\""
				fi
				if [ -n "$(echo "$COLUMN" | fgrep 'unique = true')" ]; then
					echo -n " unique=\"true\""
				fi
				echo " />"
				ENUM="$(echo "$ANNOTATIONS" | fgrep '@Enumerated')"
				if [ -n "$ENUM" ]; then
					ENUM_TYPE=$(echo "$ENUM" | sed 's|.*@Enumerated(\([^)]\+\)).*|\1|' | sed 's|EnumType\.||')
					echo "\t\t\t<enumerated>$ENUM_TYPE</enumerated>"
				fi
				echo "\t\t</basic>"
				continue
			fi
			
			IS_EMBEDDED=$(echo "$ANNOTATIONS" | fgrep '@Embedded')
			if [ -n "$IS_EMBEDDED" ]; then
				echo "\t\t<embedded name=\"$FIELD_NAME\" />"
				continue
			fi
			
			echo "$ANNOTATIONS"
			
		done
	
	echo "\t</attributes>"
	
	if [ -z "$EMBEDDABLE" ]; then
		echo "</entity>"
	else
		echo "</embeddable>"
	fi
	
done

