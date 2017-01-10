package ca.cvst.gta;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CvstCookieStore implements CookieStore {

    private Map<URI, Set<HttpCookie>> allCookies;

    public CvstCookieStore() {
        allCookies = new HashMap<>();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        uri = cookieUri(uri, cookie);
        Set<HttpCookie> targetCookies = allCookies.get(uri);
        if (targetCookies == null) {
            targetCookies = new HashSet<>();
            allCookies.put(uri, targetCookies);
        }
        targetCookies.remove(cookie);
        targetCookies.add(cookie);
        System.out.println("Adding uri = " + uri);
        System.out.println("Adding cookie = " + cookie);
    }

    private static URI cookieUri(URI uri, HttpCookie cookie) {
        URI cookieUri = uri;
        if (cookie.getDomain() != null) {
            // Remove the starting dot character of the domain, if exists (e.g: .domain.com -> domain.com)
            String domain = cookie.getDomain();
            if (domain.charAt(0) == '.') {
                domain = domain.substring(1);
            }
            try {
                cookieUri = new URI(uri.getScheme() == null ? "http"
                        : uri.getScheme(), domain,
                        cookie.getPath() == null ? "/" : cookie.getPath(), null);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return cookieUri;
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        List<HttpCookie> targetCookies = new ArrayList<>();
        // If the stored URI does not have a path then it must match any URI in
        // the same domain
        for (URI storedUri : allCookies.keySet()) {
            // Check ith the domains match according to RFC 6265
            if (checkDomainsMatch(storedUri.getHost(), uri.getHost())) {
                // Check if the paths match according to RFC 6265
                if (checkPathsMatch(storedUri.getPath(), uri.getPath())) {
                    targetCookies.addAll(allCookies.get(storedUri));
                }
            }
        }
        System.out.println("Getting uri = " + uri);
        System.out.println("Getting targetCookies = " + targetCookies);
        return targetCookies;
    }

    private boolean checkDomainsMatch(String cookieHost, String requestHost) {
        return requestHost.equals(cookieHost) || requestHost.endsWith("." + cookieHost);
    }

    private boolean checkPathsMatch(String cookiePath, String requestPath) {
        return requestPath.equals(cookiePath) ||
                (requestPath.startsWith(cookiePath) && cookiePath.charAt(cookiePath.length() - 1) == '/') ||
                (requestPath.startsWith(cookiePath) && requestPath.substring(cookiePath.length()).charAt(0) == '/');
    }

    @Override
    public List<HttpCookie> getCookies() {
        List<HttpCookie> allValidCookies = new ArrayList<>();
        for (URI storedUri : allCookies.keySet()) {
            allValidCookies.addAll(allCookies.get(storedUri));
        }

        return allValidCookies;
    }

    @Override
    public List<URI> getURIs() {
        return new ArrayList<>(allCookies.keySet());
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return false;
    }

    @Override
    public boolean removeAll() {
        return false;
    }
}
