#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <net/if.h>
#include <arpa/inet.h>
#define MAX_LINE_LENGTH 256

void resetBuffs();
void get_cpu_usage();
void get_network_usage();
void get_ram_usage();
void get_timestamp();
void get_id();
unsigned long long get_value_from_line(const char *line);
void get_memory_usage_string(char *ram, size_t ram_size);

char cpu[1000];
char ram[1000];
char net[1000];
char timestamp[1000];
char id[1000];
char* interface = "wlp2s0:";


int main(int argc, char const *argv[])
{

    char str[2] = "}\0";

    
        resetBuffs();

        get_id();
        get_timestamp();
        get_cpu_usage();
        get_memory_usage_string(ram,sizeof(ram));
        get_network_usage();


        char res[5000];
        strcpy(res,"{\n");

        strcat(res,timestamp);
        strcat(res,id);
        strcat(res, cpu);
        strcat(res, ram);
        strcat(res, net);
        strcat(res, str);

        printf("%s", res);

        res[0] = '\0';
        
    

    return 0;
}

//to change
void get_id(){

    int fd;
        struct ifreq ifr;
        char *interface = "wlp2s0"; // Change to your network interface name
        char ip_address[INET_ADDRSTRLEN]; // Buffer to store the IP address

        // Create a socket
        fd = socket(AF_INET, SOCK_DGRAM, 0);
        if (fd == -1) {
            perror("Socket creation failed");
            return ;
        }

        // Zero out the ifr structure
        memset(&ifr, 0, sizeof(ifr));

        // Specify the address family
        ifr.ifr_addr.sa_family = AF_INET;

        // Copy the interface name to ifr.ifr_name
        strncpy(ifr.ifr_name, interface, IFNAMSIZ - 1);

        // Perform the ioctl system call to get the IP address
        if (ioctl(fd, SIOCGIFADDR, &ifr) == -1) {
            perror("ioctl failed");
            close(fd);
            return ;
        }

        // Convert the IP address to a string using inet_ntop
        if (inet_ntop(AF_INET, &((struct sockaddr_in *)&ifr.ifr_addr)->sin_addr, ip_address, sizeof(ip_address)) == NULL) {
            perror("inet_ntop failed");
            close(fd);
            return ;
        }

        // Format the ID string
        snprintf(id, sizeof(id), "\"Id\" : \"%s\" ,\n", ip_address);

        close(fd);

}

void get_timestamp(){

    time_t now;
    char buffer[20];
    struct tm *timeinfo;
    time(&now);

    timeinfo = localtime(&now);
    strftime(buffer, sizeof(buffer), "%Y-%m-%dT%H:%M:%S", timeinfo);

    snprintf(timestamp, sizeof(timestamp),"\"timestamp\" : \"%s\" , \n",buffer);


}

void get_cpu_usage()
{
    FILE *fp;
    char buffer[1024 * 3];
    size_t bytes_read;
    char *match;
    float user, nice, system, idle;

    fp = fopen("/proc/stat", "r");
    if (fp == NULL)
    {
        perror("Error opening /proc/stat");
        return;
    }

    bytes_read = fread(buffer, 1, sizeof(buffer) - 1, fp);
    fclose(fp);

    if (bytes_read == 0 || bytes_read == sizeof(buffer) - 1)
    {
        fprintf(stderr, "Error reading /proc/stat or buffer overflow\n");
        if (bytes_read == sizeof(buffer) - 1)
        {
            printf("Buffer overflow\n");
        }
        return;
    }

    buffer[bytes_read] = '\0';
    match = strstr(buffer, "cpu ");
    if (match != NULL)
    {
        sscanf(match, "cpu %f %f %f %f", &user, &nice, &system, &idle);

        float total = user + nice + system + idle;
        float cpu_usage = 100 * (total - idle) / total;

        snprintf(cpu, sizeof(cpu), "\"cpu_usage\" : \"%.2f%%\" , \n", cpu_usage);
    }
}


// Function to extract integer value from a line in /proc/meminfo
unsigned long long get_value_from_line(const char *line) {
    // Find the beginning of the numeric value
    const char *ptr = line;
    while (*ptr < '0' || *ptr > '9') {
        ptr++;
    }

    // Extract the numeric value
    return strtoull(ptr, NULL, 10);
}

// Function to retrieve memory usage percentage and format as a string
void get_memory_usage_string(char *ram, size_t ram_size) {
    FILE *file = fopen("/proc/meminfo", "r");
    if (file == NULL) {
        snprintf(ram, ram_size, "\"ram_usage\" : \"Failed to open /proc/meminfo\" ,\n");
        return;
    }

    char line[MAX_LINE_LENGTH];
    unsigned long long mem_total = 0, mem_free = 0, mem_buffers = 0, mem_cached = 0;

    // Read each line from /proc/meminfo
    while (fgets(line, MAX_LINE_LENGTH, file)) {
        if (strncmp(line, "MemTotal:", 9) == 0) {
            mem_total = get_value_from_line(line);
        } else if (strncmp(line, "MemFree:", 8) == 0) {
            mem_free = get_value_from_line(line);
        } else if (strncmp(line, "Buffers:", 8) == 0) {
            mem_buffers = get_value_from_line(line);
        } else if (strncmp(line, "Cached:", 7) == 0) {
            mem_cached = get_value_from_line(line);
        }
    }

    fclose(file);

    // Calculate used memory as (MemTotal - MemFree - Buffers - Cached)
    unsigned long long mem_used = mem_total - mem_free - mem_buffers - mem_cached;

    // Calculate memory usage percentage
    double usage_percentage = ((double)mem_used / mem_total) * 100.0;

    // Format the result as a string
    snprintf(ram, ram_size, "\"ram_usage\" : \"%.2f%%\" ,\n", usage_percentage);
}


void get_network_usage()
{
    FILE *fp;
    char buffer[1024 * 3];
    size_t bytes_read;
    long rx_bytes = 0, tx_bytes = 0;
    char iface[16];

    fp = fopen("/proc/net/dev", "r");
    if (fp == NULL)
    {
        perror("Error opening /proc/net/dev");
        return;
    }

    bytes_read = fread(buffer, 1, sizeof(buffer) - 1, fp);
    fclose(fp);

    if (bytes_read == 0 || bytes_read == sizeof(buffer) - 1)
    {
        fprintf(stderr, "Error reading /proc/net/dev or buffer overflow\n");
        return;
    }

    buffer[bytes_read] = '\0';
    char *line = strtok(buffer, "\n");
    while (line != NULL)
    {
        //THIS VALUE MUST BE CONFIGURABLE
        if (strstr(line, interface) != NULL)
        {
            sscanf(line, "%s %ld %*d %*d %*d %*d %*d %*d %*d %ld", iface, &rx_bytes, &tx_bytes);
            break;
        }
        line = strtok(NULL, "\n");
    }

    snprintf(net, sizeof(net), "\"net_usage\": { \"RX\": \"%ld bytes\" , \"TX\" : \"%ld bytes\" }\n", rx_bytes, tx_bytes);
}

void resetBuffs()
{
    cpu[0] = '\0';
    ram[0] = '\0';
    net[0] = '\0';
    timestamp [0] = '\0';
    id[0] = '\0';
}
