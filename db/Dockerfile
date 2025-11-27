FROM wnameless/oracle-xe-11g-r2

# Copy initialization script
COPY ./init/script.sh /docker-entrypoint-initdb.d/init.sh

# Ensure script is executable
RUN chmod +x /docker-entrypoint-initdb.d/init.sh

# Environment variables (optional defaults)
ENV DB_USER=snippetvault
ENV DB_PASS=secret
