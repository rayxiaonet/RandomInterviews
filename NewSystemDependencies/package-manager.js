'use strict';

let packageManager = module.exports = {
  depList: {},
  installedPackages: {},
  explicitInstalls: {},
  depends: (packageName, ...dependencies) => {
    if (!packageName) {
      console.error('invalid package name');
      process.exit(1);
    }
    if (!dependencies || dependencies.length < 1) {
      console.error('invalid dependencies ');
      process.exit(1);
    }
    // console.log('- < %s > depends on %s', packageName, dependencies.map((d) => {
    //   return '<' + d + '>';
    // }).join(' and '));
    console.log('DEPEND %s %s', packageName, dependencies.join(' and '));
    if (packageManager.depList[packageName]) {
      packageManager.depList[packageName].concat(dependencies);
    } else {
      packageManager.depList[packageName] = dependencies;
    }
  },

  install: (packageName) => {
    //TODO:lock
    let result = findInstallOrder(packageName, packageManager.depList);
    console.log('INSTALL %s', packageName);
    // console.debug('full dependency list:');
    // console.dir(result);
    packageManager.explicitInstalls[packageName] = true;
    (result || []).forEach((p) => {
      if (!packageManager.installedPackages[p] && packageManager.installedPackages[p] !== 0) {
        packageManager.installedPackages[p] = 1;
        console.log('    Installing %s', p);
      } else {
        if (p === packageName) {
          console.log('    %s is already installed', p);
        }
      }
    });
    refreshRefCount(packageManager.installedPackages, packageManager.depList);
    //unlock
  },
  list: () => {
    console.log('LIST');

    Object.keys(packageManager.installedPackages).forEach((p) => {
      console.log('    %s , ref count:%s, explicit:', p, packageManager.installedPackages[p], !!packageManager.explicitInstalls[p] ? 'Y' : 'N');
    });

  },
  remove: (packageName) => {
    //TODO:lock
    console.log('REMOVE %s', packageName);
    if (packageManager.installedPackages[packageName] > 0) {
      //other package referencing this one, you cannot delete it
      console.log('    %s is still needed.', packageName);
    } else if (!packageManager.installedPackages[packageName] && packageManager.installedPackages[packageName] !== 0) {
      console.log('    %s is not installed.', packageName);
    } else {
      delete packageManager.installedPackages[packageName];
      packageManager.explicitInstalls[packageName] = false;
      let toRemove = removePackage(packageName, packageManager.depList, packageManager.installedPackages, packageManager.explicitInstalls);
      toRemove.map((p) => {
        console.log('    Removing %s', p);
      });

    }

    //unlock
  }

};


function findInstallOrder(targetPackage, orgAdjList) {
  // i.e. depends a b c => 'a':['b','c']
  //
  let adjList = {};
  //clone the adjlist
  Object.keys(orgAdjList).map((key, idx) => {
    adjList[key] = orgAdjList[key].slice(0);
  });

  let toVisit = [targetPackage];
  let visited = {};
  let order = [];
  while (toVisit.length > 0) {
    let packageName = toVisit[toVisit.length - 1];
    let depends = adjList[packageName];
    if (!!depends && depends.length > 0) {
      let dp = depends[0];
      if (visited[dp]) {
        //console.log('%s already visited', dp);
      } else {
        toVisit.push(dp);
      }
      //remove the first dependency for the adjList
      adjList[packageName].splice(0, 1);
    } else {
      //no dependent package, move forward
      order.push(packageName);
      visited[packageName] = true;
      toVisit.pop();

    }

  }
  return order;

}

function refreshRefCount(installedPackages, adjList) {
  let visited = {};
  // reset all ref count to 'P'
  Object.keys(installedPackages).forEach((p) => {
    installedPackages[p] = 0;
  });

  Object.keys(adjList).map((p, idx) => {
    if (!!installedPackages[p] || installedPackages[p] === 0) {
      //this package is installed, update the refcount
      (adjList[p] || []).forEach((depPackage) => {
        installedPackages[depPackage] += 1;
      });
    }
  });
//  console.dir(installedPackages);

}


function removePackage(targetPackage, adjList, currentPackageList, explicitInstalls) {
  let toDelete = [targetPackage];
  let visited = {};
  let order = [];
  while (toDelete.length > 0) {
    let packageName = toDelete.pop();
    order.push(packageName);

    if (!currentPackageList[packageName] || currentPackageList[packageName] === 0) {
      delete currentPackageList[packageName];
      (adjList[packageName] || []).forEach((depPackage) => {
        currentPackageList[depPackage] -= 1;
        if (currentPackageList[depPackage] === 0 && !explicitInstalls[depPackage]) {
          toDelete.push(depPackage);
        }
      });

    } else {
      console.log('ERROR cannot remove %s', packageName);
    }
  }

  return order;

}

let r = packageManager;
r.depends('TCPIP', 'NETCARD');
r.depends('TELNET', 'TCPIP', 'SOCKET');
r.depends('DNS', 'TCPIP');
r.depends('HTML', 'REGEX', 'XML');
r.depends('REGEX', 'PARSING');
r.depends('BROWSER', 'DNS', 'TCPIP', 'HTML', 'CSS');
r.install('TCPIP');
r.remove('NETCARD');
r.remove('TCPIP');
r.remove('NETCARD');
r.install('TCPIP');
r.list();
r.install('TCPIP');
r.install('foo');
r.remove('TCPIP');
r.install('NETCARD');
r.install('TCPIP');
r.remove('TCPIP');
r.list();
r.install('TCPIP');
r.install('NETCARD');
r.remove('TCPIP');
r.list();
r.remove('NETCARD');
r.install('BROWSER');
r.list();
r.remove('BROWSER');
r.list();
r.install('HTML');
r.install('TELNET');
r.remove('SOCKET');
r.install('DNS');
r.install('BROWSER');
r.remove('NETCARD');
r.list();
r.remove('BROWSER');
r.list();
