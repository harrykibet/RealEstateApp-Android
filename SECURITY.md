# Security Policy

## Supported Versions

Security updates are provided for actively maintained releases.

| Version | Supported |
|---------|-----------|
| main branch | ✅ |
| latest stable release | ✅ |
| previous stable release | ⚠️ Limited security fixes only |
| older releases | ❌ |

Only versions receiving active maintenance will receive security patches.

## Reporting a Vulnerability

The Estatia team takes security vulnerabilities seriously. If you discover a security issue, please report it responsibly.

Do not disclose security vulnerabilities publicly through GitHub Issues, Discussions, or social media.

### How to Report

Please report vulnerabilities through:

- GitHub Private Vulnerability Reporting (preferred)
- Email: security@estatia.app

Include the following information:

- Description of the vulnerability
- Affected component/module
- Steps to reproduce the issue
- Proof-of-concept or sample code (if applicable)
- Potential impact
- Suggested mitigation (if known)

## Response Timeline

After receiving a vulnerability report:

| Stage | Expected Time |
|---|---|
| Initial acknowledgement | Within 48 hours |
| Preliminary assessment | Within 7 days |
| Fix development updates | As progress is made |
| Security release | Based on severity and complexity |

## Vulnerability Handling Process

After receiving a report:

1. The security team will validate and reproduce the issue.
2. The severity and impact will be assessed.
3. A fix will be developed and tested.
4. A security update will be released.
5. Public disclosure may occur after users have had reasonable time to update.

## Severity Classification

Security issues are classified based on impact:

### Critical

Examples:

- Authentication bypass
- Remote code execution
- Exposure of sensitive user data
- Privilege escalation

### High

Examples:

- Significant data exposure
- Account compromise
- Authorization vulnerabilities

### Medium

Examples:

- Limited information disclosure
- Security weaknesses requiring specific conditions

### Low

Examples:

- Minor hardening improvements
- Low-impact issues

## Scope

Security reports related to the following areas are in scope:

- Android application security
- Authentication and authorization
- Data protection
- API security
- Cloud infrastructure
- Dependency vulnerabilities
- Cryptographic implementations

## Out of Scope

The following are generally not considered security vulnerabilities:

- Feature requests
- UI bugs
- Performance issues without security impact
- Reports requiring physical access to a device
- Social engineering attacks against users or employees

## Responsible Disclosure

We ask security researchers to:

- Avoid accessing or modifying other users' data.
- Avoid disrupting service availability.
- Provide reasonable time for remediation before public disclosure.

We appreciate responsible security research and will acknowledge valid reports that help improve the security of Estatia.